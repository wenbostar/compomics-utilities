package com.compomics.util.experiment.io.identifications.idfilereaders;

import com.compomics.util.Util;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.io.identifications.IdfileReader;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.personalization.ExperimentObject;
import com.compomics.util.waiting.WaitingHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 * This IdfileReader reads identifications from an MS Amanda csv result file.
 *
 * @author Harald Barsnes
 */
public class MsAmandaIdfileReader extends ExperimentObject implements IdfileReader {

    /**
     * The software name.
     */
    private String softwareName = "MS Amanda";
    /**
     * The softwareVersion.
     */
    private String softwareVersion = null; // not available for MS Amanda
    /**
     * The MS Amanda csv file.
     */
    private File msAmandaCsvFile;
//    /**
//     * Progress dialog for displaying the progress.
//     */
//    private static ProgressDialogX progressDialog;
    /**
     * The compomics PTM factory.
     */
    private PTMFactory ptmFactory = PTMFactory.getInstance();

//    /**
//     * Main class for testing purposes only. should be moved in a test.
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//
//        progressDialog = new ProgressDialogX(null, null, null, true);
//        progressDialog.setPrimaryProgressCounterIndeterminate(true);
//        progressDialog.setTitle("Loading PSMs. Please Wait...");
//
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    progressDialog.setVisible(true);
//                } catch (IndexOutOfBoundsException e) {
//                    // ignore
//                }
//            }
//        }, "ProgressDialog").start();
//
//        new Thread("LoadingThread") {
//            @Override
//            public void run() {
//
//                try {
//                    MsAmandaIdfileReader msAmandaIdfileReader = new MsAmandaIdfileReader(
//                            new File("C:\\Users\\hba041\\Desktop\\MS Search Engines\\MSAmanda1.4\\qExactive01819_output.csv"));
//                    msAmandaIdfileReader.getAllSpectrumMatches(progressDialog);
//                    progressDialog.setRunFinished();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
    /**
     * Default constructor for the purpose of instantiation.
     */
    public MsAmandaIdfileReader() {
    }

    /**
     * Constructor for an MS Amanda csv result file reader.
     *
     * @param msAmandaCsvFile
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MsAmandaIdfileReader(File msAmandaCsvFile) throws FileNotFoundException, IOException {
        this(msAmandaCsvFile, null);
    }

    /**
     * Constructor for an MS Amanda csv result file reader.
     *
     * @param msAmandaCsvFile
     * @param waitingHandler
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MsAmandaIdfileReader(File msAmandaCsvFile, WaitingHandler waitingHandler) throws FileNotFoundException, IOException {
        this.msAmandaCsvFile = msAmandaCsvFile;
    }

    @Override
    public String getExtension() {
        return ".csv";
    }

    @Override
    public HashSet<SpectrumMatch> getAllSpectrumMatches(WaitingHandler waitingHandler) throws IOException, IllegalArgumentException, Exception {

        HashSet<SpectrumMatch> foundPeptides = new HashSet<SpectrumMatch>();
        BufferedRandomAccessFile bufferedRandomAccessFile = new BufferedRandomAccessFile(msAmandaCsvFile, "r", 1024 * 100);

        if (waitingHandler != null) {
            waitingHandler.resetSecondaryProgressCounter();
            waitingHandler.setMaxSecondaryProgressCounter(100);
        }

        long progressUnit = bufferedRandomAccessFile.length() / 100;

        // read the header line
        String headerString = bufferedRandomAccessFile.readLine();
        String[] headers = headerString.split("\t");
        int scanNumberIndex = -1, titleIndex = -1, sequenceIndex = -1, modificationsIndex = -1, proteinAccessionsIndex = -1,
                amandaScoreIndex = -1, rankIndex = -1, mzIndex = -1, chargeIndex = -1, rtIndex = -1, filenameIndex = -1;

        // get the column index of the headers
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];

            if (header.equalsIgnoreCase("Scan Number")) {
                scanNumberIndex = i;
            } else if (header.equalsIgnoreCase("Title")) {
                titleIndex = i;
            } else if (header.equalsIgnoreCase("Sequence")) {
                sequenceIndex = i;
            } else if (header.equalsIgnoreCase("Modifications")) {
                modificationsIndex = i;
            } else if (header.equalsIgnoreCase("Protein Accessions")) {
                proteinAccessionsIndex = i;
            } else if (header.equalsIgnoreCase("Amanda Score")) {
                amandaScoreIndex = i;
            } else if (header.equalsIgnoreCase("Rank")) {
                rankIndex = i;
            } else if (header.equalsIgnoreCase("m/z")) {
                mzIndex = i;
            } else if (header.equalsIgnoreCase("Charge")) {
                chargeIndex = i;
            } else if (header.equalsIgnoreCase("RT")) {
                rtIndex = i;
            } else if (header.equalsIgnoreCase("Filename")) {
                filenameIndex = i;
            }
        }
        
        // check if all the required header are found
        if (scanNumberIndex == -1 || titleIndex == -1 || sequenceIndex == -1 || modificationsIndex == -1
                 || proteinAccessionsIndex == -1 || amandaScoreIndex == -1 || rankIndex == -1
                 || mzIndex == -1 || chargeIndex == -1 || filenameIndex == -1) {
            throw new IllegalArgumentException("Mandatory columns are missing in the MS Amanda csv file. Please check the file!");
        }

        String line;
        String currentSpectrumTitle = null;
        SpectrumMatch currentMatch = null;

        // get the psms
        while ((line = bufferedRandomAccessFile.readLine()) != null) {

            String[] elements = line.split("\t");

            if (!line.trim().isEmpty()) { // @TODO: make this more robust?
                //String scanNumber = elements[scanNumberIndex]; // not currently used
                String spectrumTitle = elements[titleIndex];
                String peptideSequence = elements[sequenceIndex].toUpperCase();
                String modifications = elements[modificationsIndex].trim();
                //String proteinAccessions = elements[proteinAccessionsIndex]; // not currently used
                String scoreAsText = elements[amandaScoreIndex];
                double score;
                try {
                    score = Double.valueOf(scoreAsText);
                } catch (NumberFormatException e) {
                    scoreAsText = scoreAsText.replaceAll("\\.", "");
                    scoreAsText = scoreAsText.replaceAll(",", "\\.");
                    score = Double.valueOf(scoreAsText);
                }
                score = Math.pow(10, -score); // convert ms amanda score to e-value
                int rank = Integer.valueOf(elements[rankIndex]);
                //String mz = elements[mzIndex]; // not currently used
                int charge = Integer.valueOf(elements[chargeIndex]);
                //double rt = Double.valueOf(elements[rtIndex]); // not currently used, and not mandatory, as old csv files didn't have this one...
                String fileName = elements[filenameIndex];

                // set up the yet empty spectrum match, or add to the current match
                if (currentMatch == null || (currentSpectrumTitle != null && !currentSpectrumTitle.equalsIgnoreCase(spectrumTitle))) {

                    // add the previous match, if any
                    if (currentMatch != null) {
                        foundPeptides.add(currentMatch);
                    }

                    currentMatch = new SpectrumMatch(Spectrum.getSpectrumKey(fileName, spectrumTitle));
                    currentSpectrumTitle = spectrumTitle;
                }

                // get the modifications
                ArrayList<ModificationMatch> utilitiesModifications = new ArrayList<ModificationMatch>();

                if (!modifications.isEmpty()) {
                    String[] ptms = modifications.split(";");

                    for (String ptm : ptms) {

                        try {
                            String residue = ptm.substring(0, 1);
                            int location = Integer.parseInt(ptm.substring(1, ptm.indexOf("(")));
                            String rest = ptm.substring(ptm.indexOf("(") + 1, ptm.length() - 1).toLowerCase();

                            String[] details = rest.split("\\|");
                            String ptmName = details[0]; // not currently used
                            String ptmMass = details[1];
                            String ptmFixedStatus = details[2];

                            if (ptmFixedStatus.equalsIgnoreCase("variable")) {
                                utilitiesModifications.add(new ModificationMatch(ptmMass + "@" + residue, true, location));
                            }
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Error parsing ptm: " + ptm + "!");
                        }
                    }
                }

                // create the peptide
                Peptide peptide = new Peptide(peptideSequence, utilitiesModifications);

                // set up the charge
                Charge peptideCharge = new Charge(Charge.PLUS, charge);

                // create the peptide assumption
                PeptideAssumption peptideAssumption = new PeptideAssumption(peptide, rank, Advocate.msAmanda.getIndex(), peptideCharge, score, Util.getFileName(msAmandaCsvFile));
                currentMatch.addHit(Advocate.msAmanda.getIndex(), peptideAssumption, true);

                if (waitingHandler != null && progressUnit != 0) {
                    waitingHandler.setSecondaryProgressCounter((int) (bufferedRandomAccessFile.getFilePointer() / progressUnit));
                    if (waitingHandler.isRunCanceled()) {
                        bufferedRandomAccessFile.close();
                        break;
                    }
                }
            }
        }

        // add the last match, if any
        if (currentMatch != null) {
            foundPeptides.add(currentMatch);
        }

        bufferedRandomAccessFile.close();

        return foundPeptides;
    }

    @Override
    public void close() throws IOException {
        msAmandaCsvFile = null;
    }

    @Override
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    @Override
    public String getSoftware() {
        return softwareName;
    }
}
