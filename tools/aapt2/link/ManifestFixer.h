/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef AAPT_LINK_MANIFESTFIXER_H
#define AAPT_LINK_MANIFESTFIXER_H

#include <string>
#include <vector>

#include "android-base/macros.h"
#include "process/IResourceTableConsumer.h"
#include "xml/XmlActionExecutor.h"
#include "xml/XmlDom.h"

namespace aapt {

struct ManifestFixerOptions {
  // The minimum SDK version to set if no 'android:minSdkVersion' is defined in a <uses-sdk> tag.
  std::optional<std::string> min_sdk_version_default;

  // The target SDK version to set if no 'android:targetSdkVersion' is defined in a <uses-sdk> tag.
  std::optional<std::string> target_sdk_version_default;

  // The Android package to use instead of the one defined in 'package' in <manifest>.
  // This also renames all relative package/class names in the manifest to fully qualified
  // Java names.
  std::optional<std::string> rename_manifest_package;

  // The Android package to use instead of the one defined in 'android:targetPackage' in
  // <instrumentation>.
  std::optional<std::string> rename_instrumentation_target_package;

  // The Android package to use instead of the one defined in 'android:targetPackage' in
  // <overlay>.
  std::optional<std::string> rename_overlay_target_package;

  // The category to use instead of the one defined in 'android:category' in <overlay>.
  std::optional<std::string> rename_overlay_category;

  // The version name to set if 'android:versionName' is not defined in <manifest> or if
  // replace_version is set.
  std::optional<std::string> version_name_default;

  // The version code to set if 'android:versionCode' is not defined in <manifest> or if
  // replace_version is set.
  std::optional<std::string> version_code_default;

  // The version code to set if 'android:versionCodeMajor' is not defined in <manifest> or if
  // replace_version is set.
  std::optional<std::string> version_code_major_default;

  // The revision code to set if 'android:revisionCode' is not defined in <manifest> or if
  // replace_version is set.
  std::optional<std::string> revision_code_default;

  // The version of the framework being compiled against to set for 'android:compileSdkVersion' in
  // the <manifest> tag. Not used if no_compile_sdk_metadata is set.
  std::optional<std::string> compile_sdk_version;

  // The version codename of the framework being compiled against to set for
  // 'android:compileSdkVersionCodename' in the <manifest> tag. Not used if no_compile_sdk_metadata
  // is set.
  std::optional<std::string> compile_sdk_version_codename;

  // The fingerprint prefixes to be added to the <install-constraints> tag.
  std::vector<std::string> fingerprint_prefixes;

  // Whether validation errors should be treated only as warnings. If this is 'true', then an
  // incorrect node will not result in an error, but only as a warning, and the parsing will
  // continue.
  bool warn_validation = false;

  // Whether to inject the android:debuggable="true" flag into the manifest
  bool debug_mode = false;

  // Whether to replace the manifest version with the the command line version
  bool replace_version = false;

  // Whether to suppress `android:compileSdkVersion*` and `platformBuildVersion*` attributes.
  bool no_compile_sdk_metadata = false;

  // Whether to mark the app as a non-updatable system app. This adds `updatableSystem="false"` to
  // the <manifest> tag. Not used if a version code is set either explicitly in the manifest or
  // through version_code_default.
  bool non_updatable_system = false;
};

// Verifies that the manifest is correctly formed and inserts defaults where specified with
// ManifestFixerOptions.
class ManifestFixer : public IXmlResourceConsumer {
 public:
  explicit ManifestFixer(const ManifestFixerOptions& options) : options_(options) {
  }

  bool Consume(IAaptContext* context, xml::XmlResource* doc) override;

 private:
  DISALLOW_COPY_AND_ASSIGN(ManifestFixer);

  bool BuildRules(xml::XmlActionExecutor* executor, IAaptContext* context);

  ManifestFixerOptions options_;
};

}  // namespace aapt

#endif /* AAPT_LINK_MANIFESTFIXER_H */
