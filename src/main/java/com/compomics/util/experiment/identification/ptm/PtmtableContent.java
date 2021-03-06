package com.compomics.util.experiment.identification.ptm;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.NeutralLoss;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.spectrum_annotation.NeutralLossesMap;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationSettings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Convenience class for the content of a PTM table.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class PtmtableContent {

    /**
     * The content of the table: modification status &gt; fragment ion type
     * according to the peptide fragment ion static fields &gt; aa number &gt;
     * list of intensities.
     */
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<Double>>>> map;
    /**
     * The total intensity.
     */
    private double totalIntensity = 0;
    /**
     * The max intensity.
     */
    private double maxIntensity = 0;

    /**
     * Constructor.
     */
    public PtmtableContent() {
        map = new HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<Double>>>>();
    }

    /**
     * Add intensity.
     *
     * @param nMod the modification number
     * @param peptideFragmentIonType the peptide fragment ion type
     * @param aa the amino acid
     * @param intensity the intensity
     */
    public void addIntensity(int nMod, Integer peptideFragmentIonType, int aa, double intensity) {
        if (!map.containsKey(nMod)) {
            map.put(nMod, new HashMap<Integer, HashMap<Integer, ArrayList<Double>>>());
        }
        if (!map.get(nMod).containsKey(peptideFragmentIonType)) {
            map.get(nMod).put(peptideFragmentIonType, new HashMap<Integer, ArrayList<Double>>());
        }
        if (!map.get(nMod).get(peptideFragmentIonType).containsKey(aa)) {
            map.get(nMod).get(peptideFragmentIonType).put(aa, new ArrayList<Double>());
        }
        map.get(nMod).get(peptideFragmentIonType).get(aa).add(intensity);
        totalIntensity += intensity;
        if (intensity > maxIntensity) {
            maxIntensity = intensity;
        }
    }

    /**
     * Get intensity.
     *
     * @param nMod the modification number
     * @param peptideFragmentIonType the peptide fragment ion type
     * @param aa the amino acid
     * @return the list of intensities
     */
    public ArrayList<Double> getIntensities(int nMod, Integer peptideFragmentIonType, int aa) {
        if (map.containsKey(nMod)
                && map.get(nMod).containsKey(peptideFragmentIonType)
                && map.get(nMod).get(peptideFragmentIonType).containsKey(aa)) {
            return map.get(nMod).get(peptideFragmentIonType).get(aa);
        } else {
            return new ArrayList<Double>();
        }
    }

    /**
     * Get the quantile.
     *
     * @param nMod the modification number
     * @param peptideFragmentIonType the peptide fragment ion type
     * @param aa the amino acid
     * @param quantile the quantile
     * @return the quantile
     */
    public Double getQuantile(int nMod, Integer peptideFragmentIonType, int aa, double quantile) {
        ArrayList<Double> intensities = getIntensities(nMod, peptideFragmentIonType, aa);
        if (intensities.size() > 0) {
            int index = (int) (quantile * intensities.size());
            return intensities.get(index);
        } else {
            return 0.0;
        }
    }

    /**
     * Get histogram.
     *
     * @param nMod the modification number
     * @param peptideFragmentIonType the peptide fragment ion type
     * @param aa the amino acid
     * @param bins the bins
     * @return the histogram
     */
    public int[] getHistogram(int nMod, Integer peptideFragmentIonType, int aa, int bins) {
        ArrayList<Double> intensities = getIntensities(nMod, peptideFragmentIonType, aa);

        int[] values = new int[bins];

        if (intensities.size() > 0) {

            for (Double intensity : intensities) {

                double currentIntensity = intensity; // / maxIntensity;
                for (int j = 0; j < bins; j++) {

                    double index = (double) j;

                    if (((index / bins) < currentIntensity) && (currentIntensity < (index + 1) / bins)) {
                        values[j]++;
                    }
                }

                // make sure that the max value is included
                if (currentIntensity == 1) {
                    values[values.length - 1]++;
                }
            }

            return values;
        } else {
            return values;
        }
    }

    /**
     * Get the map.
     *
     * @return the map
     */
    public HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<Double>>>> getMap() {
        return map;
    }

    /**
     * Add all.
     *
     * @param anotherContent another PTM table content
     */
    public void addAll(PtmtableContent anotherContent) {
        for (int nPTM : anotherContent.getMap().keySet()) {
            for (Integer peptideFragmentIonType : anotherContent.getMap().get(nPTM).keySet()) {
                for (int nAA : anotherContent.getMap().get(nPTM).get(peptideFragmentIonType).keySet()) {
                    for (double intensity : anotherContent.getIntensities(nPTM, peptideFragmentIonType, nAA)) {
                        addIntensity(nPTM, peptideFragmentIonType, nAA, intensity);
                    }
                }
            }
        }
    }

    /**
     * Normalize intensities.
     */
    public void normalize() {
        if (totalIntensity > 0) {
            double normalization = totalIntensity;
            totalIntensity = 0;
            maxIntensity = 0;
            ArrayList<Double> tempIntensities;
            for (int nPTM : map.keySet()) {
                for (Integer peptideFragmentIonType : map.get(nPTM).keySet()) {
                    for (int nAA : map.get(nPTM).get(peptideFragmentIonType).keySet()) {
                        tempIntensities = new ArrayList<Double>();
                        for (double intensity : getIntensities(nPTM, peptideFragmentIonType, nAA)) {
                            tempIntensities.add(intensity / normalization);
                        }
                        map.get(nPTM).get(peptideFragmentIonType).put(nAA, tempIntensities);
                    }
                }
            }
        }
    }

    /**
     * Returns the max intensity.
     *
     * @return the max intensity
     */
    public double getMaxIntensity() {
        return maxIntensity;
    }

    /**
     * Returns the PTM plot series in the JFreechart format for one PSM.
     *
     * @param peptide the peptide of interest
     * @param ptm the PTM to score
     * @param nPTM the amount of times the PTM is expected
     * @param spectrum the corresponding spectrum
     * @param annotationPreferences the annotation preferences
     * @param specificAnnotationPreferences the specific annotation preferences
     *
     * @return the PTM plot series in the JFreechart format for one PSM.
     */
    public static HashMap<PeptideFragmentIon, ArrayList<IonMatch>> getPTMPlotData(Peptide peptide, PTM ptm, int nPTM, MSnSpectrum spectrum,
            AnnotationSettings annotationPreferences, SpecificAnnotationSettings specificAnnotationPreferences) {

        //@TODO: use Peptide.getNoModPeptide instead
        Peptide noModPeptide = new Peptide(peptide.getSequence(), new ArrayList<ModificationMatch>());

        if (peptide.isModified()) {
            for (ModificationMatch modificationMatch : peptide.getModificationMatches()) {
                if (!modificationMatch.getTheoreticPtm().equals(ptm.getName())) {
                    noModPeptide.addModificationMatch(modificationMatch);
                }
            }
        }

        PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();
        HashMap<Integer, ArrayList<Ion>> fragmentIons
                = spectrumAnnotator.getExpectedIons(specificAnnotationPreferences, noModPeptide);
        HashMap<PeptideFragmentIon, ArrayList<IonMatch>> map = new HashMap<PeptideFragmentIon, ArrayList<IonMatch>>();

        for (int i = 0; i <= nPTM; i++) {

            spectrumAnnotator.setMassShift(i * ptm.getMass());

            ArrayList<IonMatch> matches = spectrumAnnotator.getSpectrumAnnotation(annotationPreferences, specificAnnotationPreferences, spectrum, noModPeptide);

            for (IonMatch ionMatch : matches) {
                if (ionMatch.ion.getType() == Ion.IonType.PEPTIDE_FRAGMENT_ION) {
                    PeptideFragmentIon peptideFragmentIon = (PeptideFragmentIon) ionMatch.ion;
                    for (Ion noModIon : fragmentIons.get(ionMatch.charge)) {
                        if (noModIon.getType() == Ion.IonType.PEPTIDE_FRAGMENT_ION
                                && peptideFragmentIon.isSameAs(noModIon)) {
                            PeptideFragmentIon noModFragmentIon = (PeptideFragmentIon) noModIon;
                            if (!map.containsKey(noModFragmentIon)) {
                                map.put(noModFragmentIon, new ArrayList<IonMatch>());
                            }
                            map.get(noModFragmentIon).add(ionMatch);
                            break;
                        }
                    }
                }
            }
        }

        return map;
    }

    /**
     * Get the PTM table content.
     *
     * @param peptide the peptide of interest
     * @param ptm the PTM to score
     * @param nPTM the amount of times the PTM is expected
     * @param spectrum the corresponding spectrum
     * @param annotationPreferences the annotation preferences
     * @param specificAnnotationPreferences the specific annotation preferences
     *
     * @return the PtmtableContent object
     * 
     * @throws IOException exception thrown whenever an error occurred while
     * reading a protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading a protein sequence
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws SQLException if an SQLException occurs
     */
    public static PtmtableContent getPTMTableContent(Peptide peptide, PTM ptm, int nPTM, MSnSpectrum spectrum,
            AnnotationSettings annotationPreferences, SpecificAnnotationSettings specificAnnotationPreferences) throws IOException, SQLException, ClassNotFoundException, InterruptedException {

        PtmtableContent ptmTableContent = new PtmtableContent();

        ArrayList<PTM> ptms = new ArrayList<PTM>(1);
        ptms.add(ptm);
        Peptide noModPeptide = Peptide.getNoModPeptide(peptide, ptms);

        NeutralLossesMap lossesMap = new NeutralLossesMap();
        for (String neutralLossName : specificAnnotationPreferences.getNeutralLossesMap().getAccountedNeutralLosses()) {
            NeutralLoss neutralLoss = NeutralLoss.getNeutralLoss(neutralLossName);
            if (Math.abs(neutralLoss.getMass() - ptm.getMass()) > specificAnnotationPreferences.getFragmentIonAccuracyInDa(spectrum.getMaxMz())) {
                lossesMap.addNeutralLoss(neutralLoss, 1, 1);
            }
        }

        PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();
        spectrumAnnotator.setPeptide(noModPeptide, specificAnnotationPreferences.getPrecursorCharge(), specificAnnotationPreferences);
        PeptideFragmentIon peptideFragmentIon;

        for (int i = 0; i <= nPTM; i++) {

            spectrumAnnotator.setMassShift(i * ptm.getMass());

            ArrayList<IonMatch> matches = spectrumAnnotator.getSpectrumAnnotation(annotationPreferences, specificAnnotationPreferences, spectrum, noModPeptide);

            for (IonMatch ionMatch : matches) {
                if (ionMatch.ion.getType() == Ion.IonType.PEPTIDE_FRAGMENT_ION) {
                    peptideFragmentIon = (PeptideFragmentIon) ionMatch.ion;
                    if (peptideFragmentIon.getSubType() == PeptideFragmentIon.A_ION
                            || peptideFragmentIon.getSubType() == PeptideFragmentIon.B_ION
                            || peptideFragmentIon.getSubType() == PeptideFragmentIon.C_ION) {
                        ptmTableContent.addIntensity(i, peptideFragmentIon.getSubType(), peptideFragmentIon.getNumber(), ionMatch.peak.intensity);
                    } else if (peptideFragmentIon.getSubType() == PeptideFragmentIon.X_ION
                            || peptideFragmentIon.getSubType() == PeptideFragmentIon.Y_ION
                            || peptideFragmentIon.getSubType() == PeptideFragmentIon.Z_ION) {
                        ptmTableContent.addIntensity(i, peptideFragmentIon.getSubType(),
                                peptide.getSequence().length() - peptideFragmentIon.getNumber() + 1, ionMatch.peak.intensity);
                    }
                }
            }
        }

        return ptmTableContent;
    }
}
