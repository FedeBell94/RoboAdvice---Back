package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.Quandl;

import com.jimmoores.quandl.*;
import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

@Service
public class QuandlDataSource implements IDataSource {

    private static final Log LOGGER = LogFactory.getLog(QuandlDataSource.class);

    @Autowired
    private DataRepository dataRepository;

    @Override
    public Data getData(Asset asset, Date date) {

        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");

        @SuppressWarnings("deprecation")
        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest
                        .Builder
                        .of(QuandlCodeRequest.singleColumn(
                                asset.getQuandlKey(),
                                asset.getQuandlId()))
                        .withStartDate(LocalDate.of(date.toLocalDate().getYear(), date.toLocalDate().getMonthValue(),
                                date.toLocalDate().getDayOfMonth()))
                        .build()
        );

        if (tabularResultMulti.isEmpty()) {
            LOGGER.debug("Quandl data for asset " + asset + " not found today(" + date.toLocalDate() + ")");
            return null;
        }

        Row row = tabularResultMulti.iterator().next();
        Double value = row.getDouble(1);
        return Data.builder().date(date)
                .value(BigDecimal.valueOf(value))
                .asset(asset)
                .build();
    }

    @Override
    public ArrayList<Data> getHistoricalData(Asset asset, int startYear, int startMonth, int startDay){
        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");
        @SuppressWarnings("deprecation")
        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest.Builder
                        .of(
                                QuandlCodeRequest.singleColumn(asset.getQuandlKey(), asset.getQuandlId())
                        )
                        .withStartDate(LocalDate.of(startYear, startMonth, startDay))
                        .withFrequency(Frequency.DAILY)
                        .build());
        ArrayList<Data> res = new ArrayList<>();
        for (final Row row : tabularResultMulti) {
            LocalDate ldate = row.getLocalDate("Date");
            Double value = row.getDouble(/*asset.getQuandlKey() + " - " + asset.getQuandlColumn()*/1);
            if (value != null) {
                Data d = Data.builder().date(DateTimeUtils.toSqlDate(ldate)).value(BigDecimal.valueOf(value)).asset(asset).build();
                res.add(d);
                Data dop = dataRepository.findByDateAndAsset(DateTimeUtils.toSqlDate(ldate), asset);
                if (dop == null) {
                    dataRepository.save(d);
                }
            }
        }
        return res;
    }
}
