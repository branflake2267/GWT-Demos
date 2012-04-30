package com.gonevertical.client.views.widgets.paging;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Sharding {

  /**
   * query offset
   */
  private long offset = 0;

  /**
   * query limit;
   */
  private int limit = 10;

  /**
   * finish at this count. Impose this as the total limit
   */
  private Long finish;

  /**
   * calculated end
   */
  private long end;

  /**
   * total count of
   */
  private long total;

  /**
   * amount of pages or loops
   */
  private int shards;

  /**
   * offset of base is 0 or 1, like 0,4 vs 1,5
   * app engine is true
   */
  boolean ordinalOffset = true;
  
  /**
   * class for sharding counts 
   */
  public Sharding() {
  }

  /**
   * set the query limit
   * @param offset - default starting is 0 (really it turns into 1 for db query)
   * @param limit
   */
  public void setLimit(long offset, int limit, Long finish) {
    if (offset == 0) {
      this.offset = 0;
    } else {
      this.offset = offset -1;
    }
    this.limit = limit;
    this.finish = finish;
  }

  public void setTotal(long total) {
    this.total = total;
    calcEnd();
    calcShards();
  }

  public long getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

  public Long getFinish() {
    return finish;
  }

  public long getTotal() {
    return total;
  }

  /**
   * what is the end of the query count 
   *  impose end by setting finish
   * @param es
   * @return
   */
  public long getEnd() {
    return end;
  }

  /**
   * how many shards
   * @param total
   * @param end
   * @return
   */
  public int getShards() {
    return shards;
  }

  private void calcEnd() {
    end = total;
    if (finish != null && finish < end) {
      end = finish;
    }
  }

  private void calcShards() {
    shards = 0;
    if (limit > end) {
      shards = 1;
    } else {
      BigDecimal bdoffset = new BigDecimal(end).subtract(new BigDecimal(offset));
      BigDecimal bd = bdoffset.divide(new BigDecimal(limit), MathContext.DECIMAL32).setScale(0, RoundingMode.UP);
      shards = bd.intValue();
    }
  }

  public long[] getRange(int shard) {
    BigDecimal start =  new BigDecimal(limit).multiply(new BigDecimal(shard+1)).add(new BigDecimal(offset)).subtract(new BigDecimal(limit)); 
    if (start.longValue() == 0) {
      start = new BigDecimal(1);
    } else {
      start = start.add(new BigDecimal(1));
    }
    BigDecimal stop =  new BigDecimal(limit).multiply(new BigDecimal(shard+1)).add(new BigDecimal(offset)); 
    if (stop.longValue() > end) {
      stop = new BigDecimal(end);
    }
    if (ordinalOffset == true) {
      start = start.subtract(new BigDecimal(1));
      //stop = stop.subtract(new BigDecimal(1));
    }
    long[] r = { start.longValue(), stop.intValue() };
    return r;
  }


}
