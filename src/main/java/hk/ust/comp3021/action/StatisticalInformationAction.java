package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatisticalInformationAction extends Action {
    public enum InfoKind {
        AVERAGE,
        MAXIMAL,
    };

    private InfoKind kind;

    private final Map<String, Double> actionResult = new HashMap<String, Double>();

    public StatisticalInformationAction(String id, User user, Date time, InfoKind kind) {
        super(id, user, time, ActionType.STATISTICAL_INFO);
        this.kind = kind;
    }

    public InfoKind getKind() {
        return kind;
    }

    public void setKind(InfoKind kind) {
        this.kind = kind;
    }

    public Map<String, Double> getActionResult() {
        return actionResult;
    }

    public void appendToActionResult(String key, Double value) {
        this.actionResult.put(key, value);
    }

    /**
     * TODO `obtainer1` indicates the first profiling criterion,
     *    i.e., Obtain the average number of papers published by researchers per year.
     * @param a list of papers to be profiled
     * @return `actionResult` that contains the target result
     */
    public Function<List<Paper>, Map<String, Double>> obtainer1 = paperList -> {
        Set<String> authorSet = new HashSet<String>();
        paperList.forEach(paper -> {
            paper.getAuthors().forEach(author -> authorSet.add(author));
        });
        authorSet.forEach(author -> {
            List<Paper> relatedPapers = paperList.stream()
                    .filter(paper -> paper.getAuthors().contains(author))
                    .collect(Collectors.toList());
            Entry<Integer, Long> paperNum = relatedPapers.stream()
                    .collect(Collectors.groupingBy(Paper::getYear, Collectors.counting()))
                    .entrySet().stream().reduce((entry1, entry2) -> {
                         entry1.setValue(entry1.getValue() + entry2.getValue());
                         return entry1;
                     }).get();
            int yearNum = (int) relatedPapers.stream().map(Paper::getYear).distinct().count();
            this.appendToActionResult(author, paperNum.getValue() * 1.0 / yearNum);
        });
        return this.actionResult;
    };

    /**
     * TODO `obtainer2` indicates the second profiling criterion,
     *    i.e., Obtain the journals that receive the most papers every year.
     * @param a list of papers to be profiled
     * @return `actionResult` that contains the target result
     * PS: If two journals receive the same number of papers in a given year, then we take the default order.
     */
    public Function<List<Paper>, Map<String, Double>> obtainer2 = paperList -> {
        Set<Integer> yearSet = new HashSet<Integer>();
        paperList.forEach(paper -> yearSet.add(paper.getYear()));
        yearSet.forEach(year -> {
            Entry<String, Long> maxNumJournal = paperList.stream()
                    .filter(paper -> paper.getYear() == year && paper.getJournal() != null)
                    .collect(Collectors.groupingBy(Paper::getJournal, Collectors.counting()))
                    .entrySet().stream().reduce((entry1, entry2) -> {
                        return entry1.getValue() >= entry2.getValue() ? entry1 : entry2;
                    }).get();
            this.actionResult.put(maxNumJournal.getKey(), maxNumJournal.getValue().doubleValue());
        });
        return this.actionResult;
    };

}
