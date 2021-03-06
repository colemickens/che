/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.editor.macro;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.api.resources.VirtualFile;

/**
 * Provider which is responsible for retrieving the project name of the file from the opened editor.
 *
 * <p>Macro provided: <code>${editor.current.project.name}</code>
 *
 * @see AbstractEditorMacro
 * @see EditorAgent
 * @since 4.7.0
 */
@Beta
@Singleton
public class EditorCurrentProjectNameMacro extends AbstractEditorMacro {

  public static final String KEY = "${editor.current.project.name}";

  private PromiseProvider promises;
  private final CoreLocalizationConstant localizationConstants;

  @Inject
  public EditorCurrentProjectNameMacro(
      EditorAgent editorAgent,
      PromiseProvider promises,
      CoreLocalizationConstant localizationConstants) {
    super(editorAgent);
    this.promises = promises;
    this.localizationConstants = localizationConstants;
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return KEY;
  }

  @Override
  public String getDescription() {
    return localizationConstants.macroEditorCurrentProjectNameDescription();
  }

  /** {@inheritDoc} */
  @Override
  public Promise<String> expand() {
    final EditorPartPresenter editor = getActiveEditor();

    if (editor == null) {
      return promises.resolve("");
    }

    final VirtualFile file = editor.getEditorInput().getFile();

    if (file instanceof Resource) {
      final Optional<Project> project = ((Resource) file).getRelatedProject();

      if (!project.isPresent()) {
        return promises.resolve("");
      }

      return promises.resolve(project.get().getName());
    }

    return promises.resolve("");
  }
}
