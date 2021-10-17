(defproject descriptToVideo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-clr "0.2.2"]]
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.forward/yaml "1.0.10"]
                 [org.clojure/core.async "1.3.618"]
                ;;  [com.vladsch.flexmark/flexmark-all "0.62.2"]
                 [instaparse "1.4.10"]]
  :main ^:skip-aot descript-to-video.core
  :target-path "target/%s"
  :profiles {
             :uberjar { :aot :all}}
  )
