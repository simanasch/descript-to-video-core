(ns descript-to-video.grpc-client
  (:gen-class
   :name descript-to-video.grpc-client.service.TTSServiceInpl
   :extends
   io.grpc.ttscontroller.TTSServiceGrpc$TTSServiceImplBase)
  (:import
   [io.grpc.ttscontroller TTSServiceGrpc SpeechEngineRequest SpeechEngineList]
   [io.grpc ManagedChannelBuilder]))

(def channel 
  (atom 
   (. (. (. ManagedChannelBuilder forAddress "localhost" 30051) usePlaintext) build)))
(comment

  (def stub
    (atom
     (. TTSServiceGrpc newBlockingStub @channel)))

  (def request
  ;; setNameメソッドは.protoに定義されている属性名に依存
    (. (. (. SpeechEngineRequest newBuilder) setEngineName "VOICEROID2") build))

  (def reply
    (. @stub getSpeechEngineDetail request))
  (. (. @stub getSpeechEngineDetail request) getEngineName)
  (. (. @stub getSpeechEngineDetail request) getMessage)
  ;; サーバー側を閉じたら以下実行して接続を落としておくこと
  ;; そのままだとプロセスが残る
  (. @channel shutdownNow)
  (. @channel isShutdown)

  )
