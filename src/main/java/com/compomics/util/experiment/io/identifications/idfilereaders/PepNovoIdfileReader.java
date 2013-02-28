/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.util.experiment.io.identifications.idfilereaders;

import com.compomics.util.Util;
import com.compomics.util.denovo.PeptideAssumptionDetails;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.io.identifications.IdfileReader;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.personalization.ExperimentObject;
import com.compomics.util.gui.waiting.WaitingHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 * This class can be used to parse PepNovo identification files
 *
 * @author Marc
 */
public class PepNovoIdfileReader extends ExperimentObject implements IdfileReader {

    /**
     * A map of all spectrum titles and the associated index in the random
     * access file.
     */
    private HashMap<String, Long> index;
    /**
     * The result file in random access.
     */
    private BufferedRandomAccessFile bufferedRandomAccessFile = null;
    /**
     * The name of the result file.
     */
    private String fileName;
    /**
     * The standard format 
     */
    public static final String tableHeader = "#Index	RnkScr	PnvScr	N-Gap	C-Gap	[M+H]	Charge	Sequence";

    /**
     * Default constructor for the purpose of instantiation
     */
    public PepNovoIdfileReader() {
    }

    /**
     * Constructor, initiate the parser. The close() method shall be used when
     * the file reader is no longer used.
     *
     * @param identificationFile the identification file to parse
     * @throws FileNotFoundException exception thrown whenever the provided file
     * was not found
     * @throws IOException exception thrown whenever an error occurred while
     * reading the file
     */
    public PepNovoIdfileReader(File identificationFile) throws FileNotFoundException, IOException {
        this(identificationFile, null);
    }

    /**
     * Constructor, initiate the parser. Displays the progress using the waiting
     * handler The close() method shall be used when the file reader is no
     * longer used.
     *
     * @param identificationFile the identification file to parse
     * @param waitingHandler a waiting handler providing progress feedback to
     * the user
     * @throws FileNotFoundException exception thrown whenever the provided file
     * was not found
     * @throws IOException exception thrown whenever an error occurred while
     * reading the file
     */
    public PepNovoIdfileReader(File idnetificationFile, WaitingHandler waitingHandler) throws FileNotFoundException, IOException {
        bufferedRandomAccessFile = new BufferedRandomAccessFile(idnetificationFile, "r", 1024 * 100);

        fileName = Util.getFileName(idnetificationFile);

        if (waitingHandler != null) {
            waitingHandler.setMaxSecondaryProgressValue(100);
        }
        long currentIndex = 0;
        long progressUnit = bufferedRandomAccessFile.length() / 100;

        index = new HashMap<String, Long>();

        String line, spectrumTitle;
        while ((line = bufferedRandomAccessFile.readLine()) != null) {
            if (line.startsWith(">>")) {
                currentIndex = bufferedRandomAccessFile.getFilePointer();
                int startIndex = line.indexOf(" ", 2);
                int endIndex = line.lastIndexOf("#Problem");
                if (endIndex == -1) {
                    endIndex = line.lastIndexOf("(SQS");
                }
                spectrumTitle = line.substring(startIndex, endIndex).trim();
                index.put(spectrumTitle, currentIndex);
                if (waitingHandler != null) {
                    if (waitingHandler.isRunCanceled()) {
                        break;
                    }
                    waitingHandler.setSecondaryProgressValue((int) (currentIndex / progressUnit));
                }
            }
        }
    }

    @Override
    public HashSet<SpectrumMatch> getAllSpectrumMatches(WaitingHandler waitingHandler) throws IOException, IllegalArgumentException, Exception {

        if (bufferedRandomAccessFile == null) {
            throw new IllegalStateException("The identification file was not set. Please use the appropriate constructor.");
        }

        HashSet<SpectrumMatch> spectrumMatches = new HashSet<SpectrumMatch>();

        HashSet<SpectrumMatch> result = new HashSet<SpectrumMatch>();

        if (waitingHandler != null) {
            waitingHandler.setMaxSecondaryProgressValue(index.size());
        }

        for (String title : index.keySet()) {

            SpectrumMatch currentMatch = new SpectrumMatch(Spectrum.getSpectrumKey(fileName, title));


            int cpt = 1;
            String line = bufferedRandomAccessFile.getNextLine();
            if (!line.equals(tableHeader)) {
                throw new IllegalArgumentException("Unrecognized table format. Expected: \"" + tableHeader + "\", found:\"" + line + "\".");
            }

            while ((line = bufferedRandomAccessFile.getNextLine()) != null
                    && !line.equals("") && !line.startsWith(">>")) {
                currentMatch.addHit(Advocate.PEPNOVO, getAssumptionFromLine(line, cpt));
                cpt++;
            }
            result.add(currentMatch);
            if (waitingHandler != null) {
                if (waitingHandler.isRunCanceled()) {
                    break;
                }
                waitingHandler.increaseSecondaryProgressValue();
            }

        }

        return spectrumMatches;

    }

    @Override
    public String getExtension() {
        return ".out";
    }

    @Override
    public void close() throws IOException {
        bufferedRandomAccessFile.close();
    }
    
    /**
     * Returns a Peptide Assumption from a pep novo result line. the rank score is taken as reference score. All additional parameters are attached as PeptideAssumptionDetails.
     *
     * @param line the line to parse
     * @param rank the rank of the assumption
     * @return the corresponding assumption
     */
    private PeptideAssumption getAssumptionFromLine(String line, int rank) {
        
        String[] lineComponents = line.trim().split("\t");
        
        Double rankScore = new Double(lineComponents[1]);
        Double pepNovoScore = new Double(lineComponents[2]);
        Double nGap = new Double(lineComponents[3]);
        Double cGap = new Double(lineComponents[4]);
        Integer charge = new Integer(lineComponents[6]);
        
        String sequence = lineComponents[7];
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<ModificationMatch>();
        
        Peptide peptide = new Peptide(sequence, new ArrayList<String>(), modificationMatches);
        PeptideAssumption result = new PeptideAssumption(peptide, rank, Advocate.PEPNOVO, new Charge(Charge.PLUS, charge), rankScore, fileName);
        
        PeptideAssumptionDetails peptideAssumptionDetails = new PeptideAssumptionDetails();
        peptideAssumptionDetails.setPepNovoScore(pepNovoScore);
        peptideAssumptionDetails.setcTermGap(cGap);
        peptideAssumptionDetails.setnTermGap(nGap);
        result.addUrParam(peptideAssumptionDetails);
        
        return result;
        
    }
}