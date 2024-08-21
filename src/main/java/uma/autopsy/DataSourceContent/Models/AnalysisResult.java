package uma.autopsy.DataSourceContent.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnalysisResult {

    private String itemName;
    private String aggregateScore;
    private List<AnalysisResultDetail> analysisResults;

    public AnalysisResult(String itemName, String aggregateScore) {
        this.itemName = itemName;
        this.aggregateScore = aggregateScore;
        this.analysisResults = new ArrayList<>();
    }

    public void addAnalysisResultsDetails(AnalysisResultDetail analysisResultDetail){
        this.analysisResults.add(analysisResultDetail);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalysisResultDetail {
        private String score;
        private String type;
        private String configuration;
        private String conclusion;
        private String dateCreated;
        private String deviceMake;
        private String deviceModel;
        private String comment;
        private String justification;
        private String latitude;
        private String longitude;
        private String altitude;
    }

}