package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.message.FIXUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FixTransformer implements Transformable<String, String> {

  private static final Logger logger = LogManager.getLogger(ConsoleLogNormaliser.class);

  protected Transformable<String, String> next;

  /**
   * Constructor.
   */
  public FixTransformer() {
    this(null);
  }

  /**
   * Constructor.
   *
   * @param next the next transformer in the chain to be processed.
   */
  public FixTransformer(Transformable<String, String> next) {
    this.next = next;
  }

  /**
   * Returns the value for the given tag or null if tag is not found.
   *
   * @param tag the tag to get the value from.
   * @return the value for the given tag or null if tag is not found.
   */
  public static String getTagValue(String tag, String msg) {
    String value = null;
    int start = msg.indexOf(FIXUtils.SOH_STR + tag + FIXUtils.TAG_VALUE_SEPARATOR);
    if (start >= 0) {
      value = msg.substring(start + 1, msg.indexOf(FIXUtils.SOH_STR, start + 1));
    }
    return value;
  }

  @Override
  public Transformable<String, String> getNext() {
    return next;
  }

  @Override
  public void setNext(Transformable<String, String> next) {
    this.next = next;
  }

  @Override
  public Transformable<String, String> getLast() {
    Transformable<String, String> tmp = this;
    while (tmp.getNext() != null) {
      tmp = tmp.getNext();
    }
    return tmp;
  }

  @Override
  public void append(Transformable<String, String> last) {
    getLast().setNext(last);
  }

  @Override
  public void push(Transformable<String, String> next) {
    next.append(next);
    setNext(next);
  }

  @Override
  public <C extends Transformable<String, String>> C find(Class<C> transClass) {
    return (this.getClass().getCanonicalName().equals(transClass.getCanonicalName()))
        ? (C) this
        : (next != null) ? next.find(transClass) : null;
  }

  @Override
  public String transform(String msg) {
    return (next != null) ? next.transform(msg) : msg;
  }

  @Override
  public List<Transformable<String, String>> getChain() {
    ArrayList<Transformable<String, String>> chain = new ArrayList<>();
    Transformable<String, String> tmp = this;
    while (tmp != null) {
      chain.add(tmp);
      tmp = tmp.getNext();
    }
    return chain;
  }

  @Override
  public String toString() {
    return (next != null) ? getClass().getName() + "->" + next.toString() : getClass().getName();
  }
}
