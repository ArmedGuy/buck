/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.buck.util.memory;

import static org.junit.Assume.assumeThat;

import com.facebook.buck.testutil.TestConsole;
import com.facebook.buck.util.DefaultProcessExecutor;
import com.facebook.buck.util.ProcessExecutor;
import com.facebook.buck.util.ProcessExecutorParams;
import com.facebook.buck.util.environment.Platform;
import com.facebook.buck.util.memory.ResourceMonitoringProcessExecutor.ResourceMonitoringLaunchedProcess;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class ResourceMonitoringProcessExecutorTest {
  @Test
  public void smokeTest() throws IOException, InterruptedException {
    assumeThat(Platform.detect(), Matchers.equalTo(Platform.LINUX));
    ResourceMonitoringProcessExecutor executor =
        new ResourceMonitoringProcessExecutor(new DefaultProcessExecutor(new TestConsole()));
    ProcessExecutor.LaunchedProcess process =
        executor.launchProcess(ProcessExecutorParams.ofCommand("true"));
    Assert.assertTrue(process instanceof ResourceMonitoringLaunchedProcess);
    ResourceMonitoringLaunchedProcess refined = (ResourceMonitoringLaunchedProcess) process;
    Assert.assertEquals(refined.getCommand(), ImmutableList.of("true"));
    ProcessExecutor.Result result =
        executor.execute(
            refined, ImmutableSet.of(), Optional.empty(), Optional.empty(), Optional.empty());
    Assert.assertEquals(0, result.getExitCode());
  }
}
