(ns descript-to-video.grpc.client
  (:gen-class
   :name descript-to-video.grpc-client.service.TTSServiceInpl
   :extends
   io.grpc.ttscontroller.TTSServiceGrpc$TTSServiceImplBase)
  (:import
   [io.grpc.ttscontroller TTSServiceGrpc SpeechEngineRequest SpeechEngineList ttsRequest ttsResult]
   [io.grpc ManagedChannelBuilder]))

(def channel 
  (atom 
   (. (. (. ManagedChannelBuilder forAddress "localhost" 30051) usePlaintext) build)))

;; 以下動作確認時のサンプル
(comment
  (def port 30051)
  ;; 接続
  (reset!
   channel
   (-> ManagedChannelBuilder
       (. forAddress "localhost" port)
       (. usePlaintext)
       (. build)))

  (def stub
    (atom
     (. TTSServiceGrpc newBlockingStub @channel)))
  (def request
    (-> SpeechEngineRequest
        (. newBuilder)
        (.setEngineName "葵")
        (.build)))
  (def talkRequest
    (-> (. ttsRequest newBuilder)
        (.setEngineName "葵")
        (.setBody "葵ちゃんだよ")
        (.build)))
  
  (def reply
    (. @stub getSpeechEngineDetail request))
  (def ttsReply
    (. @stub talk talkRequest))
  (println reply)
  (println ttsReply)
  (. (. @stub getSpeechEngineDetail request) getEngineName)
  (. (. @stub getSpeechEngineDetail request) getMessage)
  ;; サーバー側を閉じたら以下実行して接続を落としておくこと
  ;; そのままだとプロセスが残る
  (. @channel shutdownNow)
  (. @channel isShutdown)

  )
