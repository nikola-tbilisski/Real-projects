import dto.MeasurementToSendDTO;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DrawChart {
    public static void main(String[] args) {
        List<Double> temperatures = getTemperaturesFromServer();
        temperatures.forEach(el -> System.out.println("Degree = " + el));
        drawChart(temperatures);
    }

    private static List<Double> getTemperaturesFromServer() {
        WebClient webClient = WebClient.create("http://localhost:8080/measurements");

        return webClient.get()
                .retrieve()
                .bodyToFlux(MeasurementToSendDTO.class)
                .toStream()
                .map(MeasurementToSendDTO::getValue)
                .collect(Collectors.toList());
    }

    private static void drawChart(List<Double> temperatures) {
        double[] xData = IntStream.range(0, temperatures.size()).asDoubleStream().toArray();
        double[] yData = temperatures.stream().mapToDouble(x -> x).toArray();

        XYChart chart = QuickChart.getChart("Temperatures", "X", "Y", "temperature",
                xData, yData);

        new SwingWrapper(chart).displayChart();
    }
}
