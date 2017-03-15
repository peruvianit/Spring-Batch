package it.peruvianit.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import it.peruvianit.model.TBL1000_ITEMS;
import it.peruvianit.vo.ItemVO;

public class ItemProcessorBatch implements ItemProcessor<TBL1000_ITEMS, ItemVO> {

    private static final Logger log = LoggerFactory.getLogger(ItemProcessorBatch.class);

    @Override
    public ItemVO process(final TBL1000_ITEMS tbl1000_ITEMS) throws Exception {
    	ItemVO itemVO = new ItemVO();
    	
        log.info("PROCESS ==> prgItem: " + tbl1000_ITEMS.getPrg_item());
        
        itemVO.setProgressivoItem(tbl1000_ITEMS.getPrg_item());
        itemVO.setDescrizioneItem(tbl1000_ITEMS.getDesc_item());
        
        return itemVO;
    }
}