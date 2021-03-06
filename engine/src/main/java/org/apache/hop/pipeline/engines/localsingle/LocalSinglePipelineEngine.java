/*! ******************************************************************************
 *
 * Hop : The Hop Orchestration Platform
 *
 * http://www.project-hop.org
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.apache.hop.pipeline.engines.localsingle;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.parameters.INamedParams;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.SingleThreadedPipelineExecutor;
import org.apache.hop.pipeline.config.IPipelineEngineRunConfiguration;
import org.apache.hop.pipeline.engine.IPipelineEngine;
import org.apache.hop.pipeline.engine.PipelineEngineCapabilities;

public class LocalSinglePipelineEngine extends Pipeline implements IPipelineEngine<PipelineMeta> {

  public LocalSinglePipelineEngine() {
    super();
  }

  public LocalSinglePipelineEngine( PipelineMeta pipelineMeta ) {
    super( pipelineMeta );
  }

  public LocalSinglePipelineEngine( PipelineMeta pipelineMeta, ILoggingObject parent ) {
    super( pipelineMeta, parent );
  }

  public <Parent extends IVariables & INamedParams> LocalSinglePipelineEngine( Parent parent, String name, String filename, IHopMetadataProvider metadataProvider ) throws HopException {
    super( parent, name, filename, metadataProvider );
  }

  @Override public IPipelineEngineRunConfiguration createDefaultPipelineEngineRunConfiguration() {
    return new LocalSinglePipelineRunConfiguration();
  }

  /**
   * Should support everything
   * @return
   */
  @Override public PipelineEngineCapabilities getEngineCapabilities() {
    return new PipelineEngineCapabilities( true, true, true, true );
  }

  @Override public void prepareExecution() throws HopException {
    pipelineMeta.setPipelineType( PipelineMeta.PipelineType.SingleThreaded );
    super.prepareExecution();
  }

  @Override public void startThreads() throws HopException {
    super.startThreads();

    SingleThreadedPipelineExecutor executor = new SingleThreadedPipelineExecutor( this );

    if (!executor.init()) {
      throw new HopException( "Error initializing single threaded pipeline execution. See the log for more details." );
    }

    // Iterate until done.
    //
    while (executor.oneIteration() && !isStopped());

  }

  @Override public String getStatusDescription() {
    return super.getStatus();
  }
}
