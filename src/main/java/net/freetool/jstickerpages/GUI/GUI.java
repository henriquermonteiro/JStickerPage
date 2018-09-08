/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freetool.jstickerpages.GUI;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.DayOfWeek;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.freetool.jstickerpages.pages.StickerPage;

/**
 *
 * @author henrique
 */
public class GUI extends Application {

    private LocalDate givenDate;
    private DatePicker datePicker;
    private Spinner<Integer> spinner;
    private Button btn;

    @Override
    public void start(Stage primaryStage) {
        datePicker = new DatePicker();
        datePicker.setShowWeekNumbers(false);
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.getDayOfWeek() == DayOfWeek.SUNDAY || item.getDayOfWeek() == DayOfWeek.SATURDAY) {
                            setDisable(true);
                        }
                    }
                };
            }
        });

        datePicker.setOnAction(event -> {
            givenDate = datePicker.getValue();
            btn.setDisable(givenDate == null);
        });

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);

        spinner = new Spinner<>(valueFactory);
        spinner.setMaxWidth(100);

        Label spinnerLabel = new Label("# of Pages: ");

        FlowPane spinnerPane = new FlowPane(Orientation.HORIZONTAL);
        spinnerPane.getChildren().addAll(spinnerLabel, spinner);
        spinnerPane.setMaxWidth(200);

        btn = new Button();
        btn.setDisable(true);
        btn.setText("Print page");
        btn.setOnAction(event -> {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setCopies(1);
            job.setJobName("Sticker Page");
            job.setPrintable(new StickerPage(givenDate, spinner.getValue()));
            boolean ok = job.printDialog();
            if (ok) {
                try {
                    job.print();
                } catch (PrinterException ex) {
                    /* The job did not successfully complete */
                }
            }
        });

        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 20);
        root.setAlignment(Pos.CENTER);
        root.setColumnHalignment(HPos.CENTER);
        root.getChildren().add(datePicker);
        root.getChildren().add(spinnerPane);
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Print Sticker Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
