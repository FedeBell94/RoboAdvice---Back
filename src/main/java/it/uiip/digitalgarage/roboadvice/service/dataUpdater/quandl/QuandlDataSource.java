package it.uiip.digitalgarage.roboadvice.service.dataUpdater.quandl;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import it.uiip.digitalgarage.roboadvice.service.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.threeten.bp.DateTimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link IDataSource} interface which retrieves the data from Quandl (www.quandl.com)
 */
@Service
public class QuandlDataSource implements IDataSource {

    private static final Log LOGGER = LogFactory.getLog(QuandlDataSource.class);

    private final QuandlSession session;

    public QuandlDataSource() {
        session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");
    }

    @Override
    public List<Data> getAllDataFrom(Asset asset, CustomDate from, CustomDate to) throws ConnectionException {

        try {
            TabularResult tabularResult = session.getDataSet(
                    DataSetRequest.Builder.of(asset.getQuandlKey())
                            .withColumn(asset.getQuandlId())
                            .withStartDate(org.threeten.bp.LocalDate
                                    .of(from.getYear(), from.getMonth(), from.getDay()))
                            .withEndDate(to.getDateBpLocalDate()).build());
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
        } catch (Exception e) {
            LOGGER.error("Quandl data update failed: " + e.getMessage());
            throw new ConnectionException("Quandl can NOT download data from server.");
        }
    }
}
