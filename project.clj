(defproject descriptToVideo "0.1.0-SNAPSHOT"
  :description "テキストから各種動画作成ツールに読み込めるものを作る"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-protoc "0.4.2"]
            [lein-environ "1.2.0"]]
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "1.4.627"]
                ;;  設定ファイルとかに使う依存関係
                 [io.forward/yaml "1.0.10"]
                ;;  gRPCに使う依存関係
                 [com.google.protobuf/protobuf-java "3.6.0"]
                 [javax.annotation/javax.annotation-api "1.2"]
                 [io.netty/netty-codec-http2 "4.1.25.Final"]
                 [io.grpc/grpc-core "1.13.1"]
                 [io.grpc/grpc-netty "1.13.1"
                  :exclusions [io.grpc/grpc-core
                               io.netty/netty-codec-http2]]
                 [io.grpc/grpc-protobuf "1.13.1"]
                 [io.grpc/grpc-stub "1.13.1"]
                ;;  [instaparse "1.4.10"]
                ;; フロントエンドに使う依存関係
                 [ring "1.9.4"]
                ;;  [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-defaults "0.1.5"]
                 [metosin/ring-http-response "0.9.3"]
                 [compojure "1.6.2"]
                 [hiccup "1.0.5"]
                 [environ "1.2.0"]
                ;;  フロントエンドとの通信のうちwebsocketに使ってる依存関係
                 [http-kit "2.3.0"]
                 [jarohen/chord "0.8.1"]
                 ]
  :main ^:skip-aot descript-to-video.core

  ;; protocの読み込み関係の設定
  :protoc-version "3.6.0"
  :protoc-grpc {:version "1.13.1"}
  :proto-target-path "target/generated-sources/protobuf"
  :java-source-paths  ["target/generated-sources/protobuf"]

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :main descript-to-video.core}
             :dev {:aot :all
                   :dependencies [[prone "0.8.2"]
                                  [alembic "0.3.2"]]
                   :env {:dev true}}}
  )
