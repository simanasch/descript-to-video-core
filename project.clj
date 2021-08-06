(defproject descript-to-video "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                ;;  [clojusc/protobuf "3.5.1-v1.1"]
                ;;  [com.google.protobuf/protobuf-java "2.6.1"]
                 [com.google.protobuf/protobuf-java "3.6.0"]
                 [javax.annotation/javax.annotation-api "1.2"]
                 [io.netty/netty-codec-http2 "4.1.25.Final"]
                 [io.grpc/grpc-core "1.13.1"]
                 [io.grpc/grpc-netty "1.13.1"
                  :exclusions [io.grpc/grpc-core
                               io.netty/netty-codec-http2]]
                 [io.grpc/grpc-protobuf "1.13.1"]
                 [io.grpc/grpc-stub "1.13.1"]

                 [instaparse "1.4.10"]]

  :protoc-version "3.6.0"
  :protoc-grpc {:version "1.13.1"}
  :proto-target-path "target/generated-sources/protobuf"
  :plugins [[lein-protoc "0.4.2"]]
  
  :java-source-paths  ["target/generated-sources/protobuf"]

  :main ^:skip-aot descript-to-video.core
  :target-path "target/%s"
  ;; :java-source-paths ["target/your"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
