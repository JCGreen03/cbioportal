package org.cbioportal.legacy.service.impl;

import org.cbioportal.legacy.model.CNA;
import org.cbioportal.legacy.model.DiscreteCopyNumberData;
import org.cbioportal.legacy.model.Gene;
import org.cbioportal.legacy.model.GeneFilterQuery;
import org.cbioportal.legacy.model.GeneMolecularData;
import org.cbioportal.legacy.model.MolecularProfile;
import org.cbioportal.legacy.model.meta.BaseMeta;
import org.cbioportal.legacy.persistence.DiscreteCopyNumberRepository;
import org.cbioportal.legacy.service.MolecularDataService;
import org.cbioportal.legacy.service.MolecularProfileService;
import org.cbioportal.legacy.service.exception.MolecularProfileNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class DiscreteCopyNumberServiceImplTest extends BaseServiceImplTest {

    @InjectMocks
    private DiscreteCopyNumberServiceImpl discreteCopyNumberService;

    @Mock
    private DiscreteCopyNumberRepository discreteCopyNumberRepository;
    @Mock
    private MolecularDataService molecularDataService;
    @Mock
    private MolecularProfileService molecularProfileService;

    @Test
    public void getDiscreteCopyNumbersInMultipleMolecularProfilesHomdelOrAmp() {
        List<DiscreteCopyNumberData> returned = Arrays.asList(
            discreteCopyNumberData("sample1", "study1", -2),
            discreteCopyNumberData("sample2", "study2", 2)
        );
        List<String> profiles = Arrays.asList("profile1", "profile2");
        List<String> samples = Arrays.asList("sample1", "sample2");
        List<Integer> geneIds = Arrays.asList(0, 1);
        List<Integer> alterationTypes = Arrays.asList(-2, 2);
        Mockito.when(discreteCopyNumberRepository.getDiscreteCopyNumbersInMultipleMolecularProfiles(
                profiles,
                samples,
                geneIds,
                alterationTypes,
                PROJECTION
            ))
            .thenReturn(
                returned
            );

        List<DiscreteCopyNumberData> actual = discreteCopyNumberService.getDiscreteCopyNumbersInMultipleMolecularProfiles(
            profiles, samples, geneIds, alterationTypes, PROJECTION
        );

        Assert.assertEquals(toStrings(returned), toStrings(actual));
    }

    @Test
    public void getDiscreteCopyNumbersWithAnnotationJson() {
        List<DiscreteCopyNumberData> returned = Arrays.asList(
            discreteCopyNumberData("sample1", "study1", -2),
            discreteCopyNumberData("sample2", "study2", 2)
        );
        returned.get(0).setAnnotationJson("{\"columnName\":{\"fieldName\":\"fieldValue\"}}");
        List<String> profiles = Arrays.asList("profile1", "profile2");
        List<String> samples = Arrays.asList("sample1", "sample2");
        List<Integer> geneIds = Arrays.asList(0, 1);
        List<Integer> alterationTypes = Arrays.asList(-2, 2);

        Mockito.when(discreteCopyNumberRepository.getDiscreteCopyNumbersInMultipleMolecularProfiles(
                profiles,
                samples,
                geneIds,
                alterationTypes,
                PROJECTION
            ))
            .thenReturn(
                returned
            );

        List<DiscreteCopyNumberData> actual = discreteCopyNumberService.getDiscreteCopyNumbersInMultipleMolecularProfiles(
            profiles, samples, geneIds, alterationTypes, PROJECTION
        );

        Assert.assertEquals(toStrings(returned), toStrings(actual));
    }

    @Test
    public void getDiscreteCopyNumbersWithoutAnnotationJson() {
        List<DiscreteCopyNumberData> returned = Arrays.asList(
            discreteCopyNumberData("sample1", "study1", -2),
            discreteCopyNumberData("sample2", "study2", 2)
        );
        List<String> profiles = Arrays.asList("profile1", "profile2");
        List<String> samples = Arrays.asList("sample1", "sample2");
        List<Integer> geneIds = Arrays.asList(0, 1);
        List<Integer> alterationTypes = Arrays.asList(-2, 2);

        Mockito.when(discreteCopyNumberRepository.getDiscreteCopyNumbersInMultipleMolecularProfiles(
                profiles,
                samples,
                geneIds,
                alterationTypes,
                PROJECTION
            ))
            .thenReturn(
                returned
            );

        List<DiscreteCopyNumberData> actual = discreteCopyNumberService.getDiscreteCopyNumbersInMultipleMolecularProfiles(
            profiles, samples, geneIds, alterationTypes, PROJECTION
        );
        Assert.assertNull(returned.get(0).getAnnotationJson());
        Assert.assertEquals(toStrings(returned), toStrings(actual));
    }

    @Test
    public void getDiscreteCopyNumbersInMultipleMolecularProfilesAllAlterationTypes() {
        List<GeneMolecularData> returned = Arrays.asList(
            geneMolecularData("sample1", "study1", "-2"),
            geneMolecularData("sample2", "study1", "-1"),
            geneMolecularData("sample3", "study1", "0"),
            geneMolecularData("sample4", "study1", "1"),
            geneMolecularData("sample5", "study2", "2")
        );

        List<String> profiles = Arrays.asList("profile1", "profile2");
        List<String> samples = Arrays.asList("sample1", "sample2");
        List<CNA> alterationTypes = Arrays.asList(CNA.AMP, CNA.HOMDEL, CNA.DIPLOID, CNA.GAIN, CNA.HETLOSS);
        GeneFilterQuery query = new GeneFilterQuery();
        query.setAlterations(alterationTypes);
        List<GeneFilterQuery> geneQueries = Arrays.asList(query);
        Mockito.when(molecularDataService.getMolecularDataInMultipleMolecularProfilesByGeneQueries(
                anyList(),
                anyList(),
                anyList(),
                anyString()
            ))
            .thenReturn(returned);

        List<DiscreteCopyNumberData> actual = discreteCopyNumberService.getDiscreteCopyNumbersInMultipleMolecularProfilesByGeneQueries(
            profiles, samples, geneQueries, PROJECTION
        );
        List<DiscreteCopyNumberData> expected = Arrays.asList(
            discreteCopyNumberData("sample1", "study1", -2),
            discreteCopyNumberData("sample2", "study1", -1),
            discreteCopyNumberData("sample3", "study1", 0),
            discreteCopyNumberData("sample4", "study1", 1),
            discreteCopyNumberData("sample5", "study2", 2)
        );

        Assert.assertEquals(toStrings(expected), toStrings(actual));
    }

    @Test
    public void getDiscreteCopyNumbersInMultipleMolecularProfilesEmptyAlterationTypes() {
        List<GeneMolecularData> returned = Arrays.asList(
            geneMolecularData("sample1", "study1", "-2"),
            geneMolecularData("sample2", "study1", "-1"),
            geneMolecularData("sample3", "study1", "0"),
            geneMolecularData("sample4", "study1", "1"),
            geneMolecularData("sample5", "study2", "2")
        );

        List<String> profiles = Arrays.asList("profile1", "profile2");
        List<String> samples = Arrays.asList("sample1", "sample2");
        List<CNA> alterationTypes = Collections.emptyList();
        GeneFilterQuery query = new GeneFilterQuery();
        query.setAlterations(alterationTypes);
        List<GeneFilterQuery> geneQueries = Arrays.asList(query);

        List<DiscreteCopyNumberData> actual = discreteCopyNumberService.getDiscreteCopyNumbersInMultipleMolecularProfilesByGeneQueries(
            profiles, samples, geneQueries, PROJECTION
        );
        Assert.assertEquals(Collections.emptyList(), toStrings(actual));
    }

    @Test
    public void getDiscreteCopyNumbersInMolecularProfileBySampleListIdHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<DiscreteCopyNumberData> expectedDiscreteCopyNumberDataList = new ArrayList<>();
        DiscreteCopyNumberData discreteCopyNumberData = new DiscreteCopyNumberData();
        expectedDiscreteCopyNumberDataList.add(discreteCopyNumberData);

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-2);

        Mockito.when(discreteCopyNumberRepository.getDiscreteCopyNumbersInMolecularProfileBySampleListId(
                MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID, Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes, PROJECTION))
            .thenReturn(expectedDiscreteCopyNumberDataList);

        List<DiscreteCopyNumberData> result = discreteCopyNumberService
            .getDiscreteCopyNumbersInMolecularProfileBySampleListId(MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID,
                Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes, PROJECTION);

        Assert.assertEquals(expectedDiscreteCopyNumberDataList, result);
    }

    @Test
    public void getDiscreteCopyNumbersInMolecularProfileBySampleListIdNonHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<GeneMolecularData> expectedMolecularDataList = new ArrayList<>();
        GeneMolecularData molecularData = new GeneMolecularData();
        molecularData.setValue("-1");
        molecularData.setMolecularProfileId(MOLECULAR_PROFILE_ID);
        molecularData.setSampleId(SAMPLE_ID1);
        molecularData.setEntrezGeneId(ENTREZ_GENE_ID_1);
        Gene gene = new Gene();
        molecularData.setGene(gene);
        expectedMolecularDataList.add(molecularData);

        Mockito.when(molecularDataService.getMolecularData(MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID,
            Arrays.asList(ENTREZ_GENE_ID_1), PROJECTION)).thenReturn(expectedMolecularDataList);

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-1);

        List<DiscreteCopyNumberData> result = discreteCopyNumberService
            .getDiscreteCopyNumbersInMolecularProfileBySampleListId(MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID,
                Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes, PROJECTION);

        Assert.assertEquals(1, result.size());
        DiscreteCopyNumberData discreteCopyNumberData = result.get(0);
        Assert.assertEquals((Integer) (-1), discreteCopyNumberData.getAlteration());
        Assert.assertEquals(MOLECULAR_PROFILE_ID, discreteCopyNumberData.getMolecularProfileId());
        Assert.assertEquals(SAMPLE_ID1, discreteCopyNumberData.getSampleId());
        Assert.assertEquals(ENTREZ_GENE_ID_1, discreteCopyNumberData.getEntrezGeneId());
        Assert.assertEquals(gene, discreteCopyNumberData.getGene());
    }

    @Test
    public void getMetaDiscreteCopyNumbersInMolecularProfileBySampleListIdHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-2);

        BaseMeta expectedBaseMeta = new BaseMeta();
        Mockito.when(discreteCopyNumberRepository.getMetaDiscreteCopyNumbersInMolecularProfileBySampleListId(
                MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID, Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes))
            .thenReturn(expectedBaseMeta);

        BaseMeta result = discreteCopyNumberService.getMetaDiscreteCopyNumbersInMolecularProfileBySampleListId(
            MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID, Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes);

        Assert.assertEquals(expectedBaseMeta, result);
    }

    @Test
    public void getMetaDiscreteCopyNumbersInMolecularProfileBySampleListIdNonHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<GeneMolecularData> expectedMolecularDataList = new ArrayList<>();
        GeneMolecularData molecularData = new GeneMolecularData();
        molecularData.setValue("-1");
        expectedMolecularDataList.add(molecularData);

        Mockito.when(molecularDataService.getMolecularData(MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID,
            Arrays.asList(ENTREZ_GENE_ID_1), "ID")).thenReturn(expectedMolecularDataList);

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-1);

        BaseMeta result = discreteCopyNumberService.getMetaDiscreteCopyNumbersInMolecularProfileBySampleListId(
            MOLECULAR_PROFILE_ID, SAMPLE_LIST_ID, Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes);

        Assert.assertEquals((Integer) 1, result.getTotalCount());
    }

    @Test
    public void fetchDiscreteCopyNumbersInMolecularProfileHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<DiscreteCopyNumberData> expectedDiscreteCopyNumberDataList = new ArrayList<>();
        DiscreteCopyNumberData discreteCopyNumberData = new DiscreteCopyNumberData();
        expectedDiscreteCopyNumberDataList.add(discreteCopyNumberData);

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-2);

        Mockito.when(discreteCopyNumberRepository.fetchDiscreteCopyNumbersInMolecularProfile(MOLECULAR_PROFILE_ID,
                Arrays.asList(SAMPLE_ID1), Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes, PROJECTION))
            .thenReturn(expectedDiscreteCopyNumberDataList);

        List<DiscreteCopyNumberData> result = discreteCopyNumberService.fetchDiscreteCopyNumbersInMolecularProfile(
            MOLECULAR_PROFILE_ID, Arrays.asList(SAMPLE_ID1), Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes, PROJECTION);

        Assert.assertEquals(expectedDiscreteCopyNumberDataList, result);
    }

    @Test
    public void fetchDiscreteCopyNumbersInMolecularProfileNonHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<GeneMolecularData> expectedMolecularDataList = new ArrayList<>();
        GeneMolecularData molecularData = new GeneMolecularData();
        molecularData.setValue("-1");
        molecularData.setMolecularProfileId(MOLECULAR_PROFILE_ID);
        molecularData.setSampleId(SAMPLE_ID1);
        molecularData.setEntrezGeneId(ENTREZ_GENE_ID_1);
        Gene gene = new Gene();
        molecularData.setGene(gene);
        expectedMolecularDataList.add(molecularData);

        Mockito.when(molecularDataService.fetchMolecularData(MOLECULAR_PROFILE_ID, Arrays.asList(SAMPLE_ID1),
            Arrays.asList(ENTREZ_GENE_ID_1), PROJECTION)).thenReturn(expectedMolecularDataList);

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-1);

        List<DiscreteCopyNumberData> result = discreteCopyNumberService.fetchDiscreteCopyNumbersInMolecularProfile(
            MOLECULAR_PROFILE_ID, Arrays.asList(SAMPLE_ID1), Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes, PROJECTION);

        Assert.assertEquals(1, result.size());
        DiscreteCopyNumberData discreteCopyNumberData = result.get(0);
        Assert.assertEquals((Integer) (-1), discreteCopyNumberData.getAlteration());
        Assert.assertEquals(MOLECULAR_PROFILE_ID, discreteCopyNumberData.getMolecularProfileId());
        Assert.assertEquals(SAMPLE_ID1, discreteCopyNumberData.getSampleId());
        Assert.assertEquals(ENTREZ_GENE_ID_1, discreteCopyNumberData.getEntrezGeneId());
        Assert.assertEquals(gene, discreteCopyNumberData.getGene());
    }

    @Test
    public void fetchMetaDiscreteCopyNumbersInMolecularProfileHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-2);

        BaseMeta expectedBaseMeta = new BaseMeta();
        Mockito.when(discreteCopyNumberRepository.fetchMetaDiscreteCopyNumbersInMolecularProfile(MOLECULAR_PROFILE_ID,
            Arrays.asList(SAMPLE_ID1), Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes)).thenReturn(expectedBaseMeta);

        BaseMeta result = discreteCopyNumberService.fetchMetaDiscreteCopyNumbersInMolecularProfile(
            MOLECULAR_PROFILE_ID, Arrays.asList(SAMPLE_ID1), Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes);

        Assert.assertEquals(expectedBaseMeta, result);
    }

    @Test
    public void fetchMetaDiscreteCopyNumbersInMolecularProfileNonHomdelOrAmp() throws Exception {

        createMolecularProfile();

        List<GeneMolecularData> expectedMolecularDataList = new ArrayList<>();
        GeneMolecularData molecularData = new GeneMolecularData();
        molecularData.setValue("-1");
        expectedMolecularDataList.add(molecularData);

        Mockito.when(molecularDataService.fetchMolecularData(MOLECULAR_PROFILE_ID, Arrays.asList(SAMPLE_ID1),
            Arrays.asList(ENTREZ_GENE_ID_1), "ID")).thenReturn(expectedMolecularDataList);

        List<Integer> alterationTypes = new ArrayList<>();
        alterationTypes.add(-1);

        BaseMeta result = discreteCopyNumberService.fetchMetaDiscreteCopyNumbersInMolecularProfile(
            MOLECULAR_PROFILE_ID, Arrays.asList(SAMPLE_ID1), Arrays.asList(ENTREZ_GENE_ID_1), alterationTypes);

        Assert.assertEquals((Integer) 1, result.getTotalCount());
    }

    private void createMolecularProfile() throws MolecularProfileNotFoundException {

        MolecularProfile molecularProfile = new MolecularProfile();
        molecularProfile.setMolecularAlterationType(MolecularProfile.MolecularAlterationType.COPY_NUMBER_ALTERATION);
        molecularProfile.setDatatype("DISCRETE");
        Mockito.when(molecularProfileService.getMolecularProfile(MOLECULAR_PROFILE_ID)).thenReturn(molecularProfile);
    }

    private DiscreteCopyNumberData discreteCopyNumberData(String sample, String study, Integer alteration) {
        DiscreteCopyNumberData data = new DiscreteCopyNumberData();
        data.setStudyId(study);
        data.setSampleId(sample);
        data.setAlteration(alteration);

        return data;
    }

    private GeneMolecularData geneMolecularData(String sample, String study, String alteration) {
        GeneMolecularData data = new GeneMolecularData();
        data.setSampleId(sample);
        data.setStudyId(study);
        data.setValue(alteration);

        return data;
    }

    // I don't want to pollute DiscreteCopyNumberData with an equals that
    // isn't necessarily accurate for anything outside this very narrow test
    // so here's a quick string conversion method instead.
    private List<String> toStrings(List<DiscreteCopyNumberData> data) {
        return data.stream()
            .map(d -> d.getAlteration() + " " + d.getStudyId() + " " + d.getSampleId())
            .collect(Collectors.toList());
    }
}
