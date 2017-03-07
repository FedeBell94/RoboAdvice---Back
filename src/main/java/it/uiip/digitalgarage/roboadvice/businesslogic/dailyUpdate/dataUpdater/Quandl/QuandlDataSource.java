package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.Quandl;

import com.jimmoores.quandl.*;
import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class QuandlDataSource implements IDataSource {

    @Override
    public Data getData(Asset asset, Date date) {

        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");

        @SuppressWarnings("deprecation")
        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest
                        .Builder
                        .of(QuandlCodeRequest.singleColumn(asset.getQuandlKey(), asset.getQuandlId()))
                        .withStartDate(LocalDate.of(date.toLocalDate().getYear(), date.toLocalDate().getMonthValue(),
                                date.toLocalDate().getDayOfMonth()))
                        .build()
        );

        if (tabularResultMulti.isEmpty()) {
            Logger.debug(QuandlDataSource.class,
                    "Quandl data for asset " + asset + " not found today(" + date.toLocalDate() + ")");
            return null;
        }

        Row row = tabularResultMulti.iterator().next();
        Double value = row.getDouble(1);
        return Data.builder().date(date)
                .value(BigDecimal.valueOf(value))
                .asset(asset)
                .build();
    }
}
