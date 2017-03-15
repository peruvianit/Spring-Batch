package it.peruvianit.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import it.peruvianit.vo.ItemVO;

public class ItemWriterBatch implements ItemWriter<ItemVO> {

    private static final Logger log = LoggerFactory.getLogger(ItemWriterBatch.class);

	@Override
	public void write(List<? extends ItemVO> listItemVO) throws Exception {
		for (ItemVO itemVO2 : listItemVO) {
			log.info("WRITER ===> PrgItem : " + itemVO2.getProgressivoItem() + ", descrizione : " + itemVO2.getDescrizioneItem());
		}
    }
}