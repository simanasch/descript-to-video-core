(ns descript-to-video.core
  (:gen-class)
  (:require [descript-to-video.tts :as tts]
            [descript-to-video.markdown.parser :as mdparser]
            [descript-to-video.grpc.service])
  (:import [io.grpc Server ServerBuilder]
           [io.grpc.stub StreamObserver]
           [descript-to-video.grpc.service GreeterServiceImpl]
           ))

(def SERVER_PORT 30051)

(defn start []
  (let [greeter-service (new GreeterServiceImpl)
        server (-> (ServerBuilder/forPort SERVER_PORT)
                   (.addService greeter-service)
                   (.build)
                   (.start))]
    (-> (Runtime/getRuntime)
        (.addShutdownHook
         (Thread. (fn []
                    (if (not (nil? server))
                      (.shutdown server))))))
    (if (not (nil? server))
      (.awaitTermination server))))

(defn -main
  [& args]
  ;; TODO:引数の値による処理分岐
  ;; とりあえず出力のファイル形式は変えられるようにする
  (apply println "Received args:" args)
  (print "Now listening on port " SERVER_PORT)
  (start)
  ;; (import '(descript-to-video.grpc.service GreeterServiceImpl))
  ;; (descript-to-video.grpc.service/-sayHello)
  (let [targetPath (cond (string? args) args :else "E://Documents/descript-to-video/sample/sample.md")
        ;; "E://Documents/descript-to-video/sample/sample.md"
        lib-text  (mdparser/get-voiceroid-text-lines (slurp targetPath))]
    (println "result:" lib-text)
    (tts/save-to-files lib-text)
    (shutdown-agents)
  ))