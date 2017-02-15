/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.ui;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.tree.TreeUtil;
import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import org.jetbrains.annotations.NonNls;
import org.sonarlint.intellij.core.ProjectBindingManager;
import org.sonarlint.intellij.issue.LiveIssue;
import org.sonarlint.intellij.ui.scope.AbstractScope;
import org.sonarlint.intellij.ui.scope.CurrentFileController;

public class SonarLintIssuesPanel extends AbstractIssuesPanel implements DataProvider {
  private static final String GROUP_ID = "SonarLint.issuestoolwindow";
  private static final String SPLIT_PROPORTION_PROPERTY = "SONARLINT_ISSUES_SPLIT_PROPORTION";

  private final CurrentFileController scope;

  public SonarLintIssuesPanel(Project project, ProjectBindingManager projectBindingManager, CurrentFileController scope) {
    super(project, projectBindingManager);
    this.scope = scope;

    // Issues panel
    setToolbar(GROUP_ID);
    JPanel issuesPanel = new JPanel(new BorderLayout());
    issuesPanel.add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);
    issuesPanel.add(new AutoTriggerStatusPanel(project).getPanel(), BorderLayout.SOUTH);

    super.setContent(createSplitter(issuesPanel, detailsTab, SPLIT_PROPORTION_PROPERTY, false, 0.65f));

    subscribeToEvents();
  }

  private void subscribeToEvents() {
    scope.setPanel(this);
  }

  public void update(@Nullable VirtualFile file, Collection<LiveIssue> issues, String emptyText) {
    if (file == null) {
      treeBuilder.updateModel(Collections.emptyMap(), emptyText);
    } else {
      treeBuilder.updateModel(Collections.singletonMap(file, issues), emptyText);
    }
    expandTree();
  }

  private void expandTree() {
    TreeUtil.expandAll(tree);
  }

  @Nullable
  @Override
  public Object getData(@NonNls String dataId) {
    if (AbstractScope.SCOPE_DATA_KEY.is(dataId)) {
      return scope;
    }

    return null;
  }
}
