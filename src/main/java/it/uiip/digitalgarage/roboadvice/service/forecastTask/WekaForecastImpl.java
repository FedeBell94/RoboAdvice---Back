package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.persistence.model.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import jersey.repackaged.com.google.common.collect.Iterators;
import jersey.repackaged.com.google.common.collect.Lists;
import weka.core.*;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.WekaForecaster;

import java.time.temporal.ChronoUnit;

/**
 * Created by Simone on 27/03/2017.
 */
public class WekaForecastImpl implements IDataForecastComputation {

    @Override public Map<Date, BigDecimal> computeForecast(Iterable<Data> data, LocalDate to) {

        LocalDate today = LocalDate.now();
        CustomDate customDate = new CustomDate(today);
        long daysSpanLong = ChronoUnit.DAYS.between(today, to);
        Integer daysSpan = (int) (long) daysSpanLong;
        Map<Date, BigDecimal> result = null;
        List<Data> dataList = Lists.newArrayList(data);

        //Provided data granularity check
        Boolean isDaily;
        long dataGranularity = ChronoUnit.DAYS.between(dataList.get(1).getDate().toLocalDate(), dataList.get(0).getDate().toLocalDate());
        if (dataGranularity < 15) {
            isDaily = true;
        } else {
            isDaily = false;
        }

        if (!isDaily) {
            daysSpan = daysSpan / 30;
        }

        try {

            // Declare a numeric attribute
            Attribute assetValue = new Attribute("assetValue");
            Attribute date = new Attribute("Date", "yyyy-MM-dd");

            // Declare the feature vector
            FastVector fvWekaAttributes = new FastVector(2);
            fvWekaAttributes.addElement(assetValue);
            fvWekaAttributes.addElement(date);

            Instances dataSet = new Instances("MyRelation", fvWekaAttributes, Iterators.size(data.iterator()));

            // Set class index
            List<Instance> setEntries = new ArrayList<>();
            LocalDate tmpDate = null;

            for (Data curData : data) {

                double[] attValues = new double[dataSet.numAttributes()];
                // Create the instance
                attValues[0] = curData.getValue().doubleValue();

                tmpDate = curData.getDate().toLocalDate();
                attValues[1] = dataSet.attribute("Date").parseDate(tmpDate.toString());

                setEntries.add(new DenseInstance(1.0, attValues));

            }
            Collections.reverse(setEntries);
            for (Instance curVal : setEntries) {
                dataSet.add(curVal);
            }

            // new forecaster
            WekaForecaster forecaster = new WekaForecaster();

            // set the targets we want to forecast. This method calls
            // setFieldsToLag() on the lag maker object for us
            forecaster.setFieldsToForecast("assetValue");

            // default underlying classifier is SMOreg (SVM) - we'll use
            // gaussian processes for regression instead
            forecaster.setBaseForecaster(new GaussianProcesses());

            forecaster.getTSLagMaker().setTimeStampField("Date"); // date time stamp
//            forecaster.getTSLagMaker().setMinLag(1);
//            forecaster.getTSLagMaker().setMaxLag(12); // monthly or daily data

            // add a month of the year indicator field
            forecaster.getTSLagMaker().setAddMonthOfYear(true);

            // add a quarter of the year indicator field
            forecaster.getTSLagMaker().setAddQuarterOfYear(true);

            // build the model
            forecaster.buildForecaster(dataSet, System.out);

            // prime the forecaster with enough recent historical data
            // to cover up to the maximum lag. In our case, we could just supply
            // the most recent historical instances, as this covers our maximum
            // lag period
            forecaster.primeForecaster(dataSet);

            // forecast for n units (daily/montly) beyond the end of the
            // training data
            List<List<NumericPrediction>> forecast = forecaster.forecast(daysSpan, System.out);

            // output the predictions. Outer list is over the steps; inner list is over
            // the targets
            result = new HashMap<>();
            for (int i = 0; i < daysSpan; i++) {
                List<NumericPrediction> predsAtStep = forecast.get(i);
                // for (int j = 0; j < 2; j++) {
                NumericPrediction predForTarget = predsAtStep.get(0);
                BigDecimal prediction = BigDecimal.valueOf(predForTarget.predicted());
//                System.out.print("" + predForTarget.predicted() + " ");
                // }
//                System.out.println();
                if (isDaily) {
                    result.put(customDate.getDayFromSql(i), prediction);
                } else {
                    result.put(customDate.getDayFromSql(i + 30), prediction);
                }
            }

            // we can continue to use the trained forecaster for further forecasting
            // by priming with the most recent historical data (as it becomes available).
            // At some stage it becomes prudent to re-build the model using current
            // historical data.

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

}
