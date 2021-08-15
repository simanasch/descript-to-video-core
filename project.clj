(defproject descript-to-video "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.forward/yaml "1.0.10"]
                 [org.clojure/core.async "1.3.618"]
                 [com.vladsch.flexmark/flexmark-all "0.62.2"]
                 [instaparse "1.4.10"]]
  ;; :repositories [["com.vladsch.flexmark/flexmark-java" "https://github.com/vsch/flexmark-java"]]
  :main ^:skip-aot descript-to-video.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
