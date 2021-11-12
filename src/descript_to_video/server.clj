(ns descript-to-video.server
  (:require
   [org.httpkit.server :refer [run-server]]
   [chord.http-kit :refer [with-channel wrap-websocket-handler]]
   [clojure.core.async :refer [<! >! put! close! go chan]])
  )

(defonce server (atom nil))
(def port 3000)

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn app [req]
  (with-channel req ws-ch
    (go
      (let [{:keys [message]} (<! ws-ch)]
        (println "Received message:" message)
        (>! ws-ch "Hello client from server!")
        (close! ws-ch)
        ))))

(defn start-server []
  (reset! server (run-server #'app {:port port})))

(comment

  (def inst (run-server (-> #'app wrap-websocket-handler) {:port 3000}))
  (start-server)
  @server
  (stop-server)
  )
