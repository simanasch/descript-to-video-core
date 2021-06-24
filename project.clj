(defproject descriptToVideo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-clr "0.2.2"]]
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [instaparse "1.4.10"]]
  :main ^:skip-aot descriptToVideo.core
  :target-path "target/%s"
  :profiles {} 
  :clr {:cmd-templates  {:clj-dep   [[?PATH "mono"] ["target/default/clr/clj/Debug 4.0" %1]]
                         :clj-url   "http://sourceforge.net/projects/clojureclr/files/clojure-clr-1.4.1-Debug-4.0.zip/download"
                         :clj-zip   "clojure-clr-1.4.1-Debug-4.0.zip"
                         :curl      ["curl" "--insecure" "-f" "-L" "-o" %1 %2]
                         :nuget-ver [[?PATH "mono"] [*PATH "nuget.exe"] "install" %1 "-Version" %2]
                         :nuget-any [[?PATH "mono"] [*PATH "nuget.exe"] "install" %1]
                         :unzip     ["unzip" "-d" %1 %2]
                         :wget      ["wget" "--no-check-certificate" "--no-clobber" "-O" %1 %2]}
        :deps-cmds      [[:curl  :clj-zip :clj-url]
                         [:unzip "../clj" :clj-zip]]
        :main-cmd      [:clj-dep "Clojure.Main.exe"]
        :compile-cmd   [:clj-dep "Clojure.Compile.exe"]})
