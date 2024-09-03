package uma.autopsy.DataSourceContent.Models;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class AnalysisResult {
    private static final AtomicInteger counter = new AtomicInteger();
    private int id;
    private String itemName;
    private String aggregateScore;
    private List<AnalysisResultDetail> analysisResults;

    public AnalysisResult() {
        this.id = counter.incrementAndGet();
    }

    @Getter
    @Setter
    public static class AnalysisResultDetail {
        private int id;
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

        public AnalysisResultDetail() {
            this.id = counter.incrementAndGet();
        }
    }
}