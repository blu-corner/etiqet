package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.client.delegate.FixClientDelegate;

/**
 * This class is in charge of running a flow.
 */
class FlowRunner extends FixClientDelegate {

  protected Transformable<String, String> flow;

  /**
   * Constructor.
   */
  public FlowRunner(Transformable<String, String> flow) {
    this.flow = flow;
  }

  @Override
  public String transformAfterEncoding(String msg) {
    return flow.transform(msg);
  }

}
