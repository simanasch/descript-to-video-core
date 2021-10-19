(ns descript-to-video.grpc.service
  (:gen-class
   :name descript-to-video.grpc.service.GreeterServiceImpl
   :extends
   io.grpc.examples.helloworld.GreeterGrpc$GreeterImplBase)
  (:import
   [io.grpc.stub StreamObserver]
   [io.grpc.examples.helloworld
    HelloReply]))


(defn -sayHello [this req res]
  (let [name (.getName req)]
    (doto res
      (.onNext (-> (HelloReply/newBuilder)
                   (.setMessage (str "Hello, " name))
                   (.build)))
      (.onCompleted))))

(defn -sayHelloAgain [this req res]
  (let [name (.getName req)]
    (doto res
      (.onNext (-> (HelloReply/newBuilder)
                   (.setMessage (str "Hello Again," name))
                   (.build)))
      (.onCompleted))))

(comment
  ;; 以下実行すると正常に実行される…ので、多分client.cljのほうはなんかミスってそう
  ;; →戻り値にnullが含まれてて落ちてたっぽい、リクエスト内容には特に問題なかった
  (import [io.grpc ManagedChannelBuilder])
  (import [io.grpc.examples.helloworld GreeterGrpc HelloRequest])
  (def channel
    (atom
     (. (. (. ManagedChannelBuilder forAddress "localhost" 30051) usePlaintext) build)))
  (def stub
    (atom (. GreeterGrpc newBlockingStub @channel)))
  (def request
    (-> HelloRequest
        (. newBuilder)
        (.setName "茜")
        (.build)))
  (. @stub sayHello request)
  (def request2
    (-> HelloRequest
        (. newBuilder)
        (.setName "葵ちゃんだよー")
        (.build)))
  (. @stub sayHelloAgain request2)
  )