(ns descript-to-video.core
  (:gen-class)
    (:require [descript-to-video.service])
  (:import
   [io.grpc
    Server
    ServerBuilder]
   [io.grpc.stub StreamObserver]
   [descript-to-video.service GreeterServiceImpl]))

(def SERVER_PORT 50051)

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
  ;; (apply println "Received args:" args)
  (print "Now listening on port " SERVER_PORT)
  (start))
