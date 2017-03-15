package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.Quandl;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.threeten.bp.DateTimeUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuandlDataSource implements IDataSource {

    private static final Log LOGGER = LogFactory.getLog(QuandlDataSource.class);

    private final QuandlSession session;

    public QuandlDataSource() {
        session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");
    }

    @Override
    public List<Data> getAllDataFrom(Asset asset, Date from) {
        return retrieveData(asset, from, org.threeten.bp.LocalDate.now());
    }

    private List<Data> retrieveData(Asset asset, Date from, org.threeten.bp.LocalDate to) {
        LocalDate fromDate = from.toLocalDate();
        TabularResult tabularResult = session.getDataSet(
                DataSetRequest.Builder.of(asset.getQuandlKey())
                        .withColumn(asset.getQuandlId())
                        .withStartDate(org.threeten.bp.LocalDate
                                .of(fromDate.getYear(), fromDate.getMonthValue(), fromDate.getDayOfMonth()))
                        .withEndDate(to).build());

        List<Data> dataList = new ArrayList<>(tabularResult.size());
        for (Row currRow : tabularResult) {
            Double value = currRow.getDouble(1);
            if (value != null) {
                org.threeten.bp.LocalDate date = currRow.getLocalDate(0);
                Data d = Data.builder().date(DateTimeUtils.toSqlDate(date)).value(BigDecimal.valueOf(value))
                        .asset(asset).build();
                dataList.add(d);
            }
        }
        return dataList;
    }
}
