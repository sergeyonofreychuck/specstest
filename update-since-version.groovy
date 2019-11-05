/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

import groovy.io.FileVisitResult

EMPTY_VERSION_TERM = "@since 3.next"
EMPTY_VERSION_REPLACE_REGEXP = /@since \d+\.next/

FILTER_FILES_EXCLUDED = /Jenkins-set-since-version/
FILTER_FOLDERS_EXCLUDED = /^\..*|^target$|^node_modules$/

VERSION_CHECK = /^(\d+\.)?(\d+\.)?(\*|\d+)$/

def version = args[0]

if (!(version ==~ VERSION_CHECK)) {
  System.err.println "Invalid version provided. \'${version}\'. Set environment variable nxrm_version " +
      "or use the first command line argument"
  System.exit(1)
}

println "The version to apply: ${version}"

def parentDirectory = new File(".")

def setVersionInNewFile = {
  def content = it.text

  if (content.contains(EMPTY_VERSION_TERM)) {
    println "Applying the version to ${it.name}"
    it.write(content.replaceAll(EMPTY_VERSION_REPLACE_REGEXP, "@since ${version}"))
  }
}

def foldersFilter = {
  return it.name ==~ FILTER_FOLDERS_EXCLUDED ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE
}

parentDirectory.traverse(
    type: groovy.io.FileType.FILES,
    visit: setVersionInNewFile,
    excludeNameFilter: FILTER_FILES_EXCLUDED,
    preDir: foldersFilter)