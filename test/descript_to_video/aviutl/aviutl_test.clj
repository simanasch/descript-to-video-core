(ns descript-to-video.aviutl.aviutl-test
  (:require
   [clojure.test :refer :all]
   [descript-to-video.aviutl.aviutl :refer :all]
   [clojure.java.io :as io]
   [descript-to-video.util.format :as formatter]
   [descript-to-video.util.map :as m]
   [descript-to-video.aviutl.parser :as parser]
   [flatland.ordered.map :refer :all]
   [descript-to-video.markdown.parser :as mdparser]
   [descript-to-video.tts.tts :as tts]
   [descript-to-video.util.audio :as a]
   [descript-to-video.markdown.marp :as marp] :reload))

(def sample-file-path "test/resources/test_sample.md")
(def sample-template-path "test/resources/test_sample.exo")
(def sample-slide-pathes '("e:\\Documents\\descript-to-video\\test\\resources\\test_sample.001.png" "e:\\Documents\\descript-to-video\\test\\resources\\test_sample.002.png" "e:\\Documents\\descript-to-video\\test\\resources\\test_sample.003.png" "e:\\Documents\\descript-to-video\\test\\resources\\test_sample.004.png" "e:\\Documents\\descript-to-video\\test\\resources\\test_sample.005.png"))
(def sample-tts-results '(({:isSuccess true, :libraryName "雫", :engineName "", :Body "こんにちは、結月ゆかり雫です", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_こんにちは、結月ゆかり雫です.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "この動画は手を抜くためになんでもしたようなツール動画…のうち今できてる内容を適当に喋る動画です。", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_この動画は手を抜くためになんでもしたようなツール動画…のうち今できてる内容を適当に喋る動画です。.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "絶賛制作中な内容になりますがお付き合いいただけると幸いです", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_絶賛制作中な内容になりますがお付き合いいただけると幸いです.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "ツッコミ役にあかねちゃんもおるで、調声できてないのは堪忍な", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_ツッコミ役にあかねちゃんもおるで、調声できてないのは堪忍な.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "では概要から", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_では概要から.wav"}) ({:isSuccess true, :libraryName "雫", :engineName "", :Body "パワポ流すタイプの解説動画を楽して作るツールです", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_パワポ流すタイプの解説動画を楽して作るツールです.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "動画でプログラム関係の解説を見ることは増えてきましたが、", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_動画でプログラム関係の解説を見ることは増えてきましたが、.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "動画の内容を文章で見たいことが多かったのでじゃあ文章から動画を作ってしまおうとなりました", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_動画の内容を文章で見たいことが多かったのでじゃあ文章から動画を作ってしまおうとなりました.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "そうはならんのでは？", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_そうはならんのでは？.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "あとはアイボスとか使いたいねというモチベです", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_あとはアイボスとか使いたいねというモチベです.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "次は実際やってることの説明やで", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_次は実際やってることの説明やで.wav"}) ({:isSuccess true, :libraryName "雫", :engineName "", :Body "今の所、実際に動画を出力するのにはaviutlを使うことにしています。", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_今の所、実際に動画を出力するのにはaviutlを使うことにしています。.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "立ち絵の設定とかは一度作っておけば使いまわしが効きそうだったので、一度テンプレートのファイルを作り、", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_立ち絵の設定とかは一度作っておけば使いまわしが効きそうだったので、一度テンプレートのファイルを作り、.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "テンプレートのファイルに対して音声とか画像を足して動画にできるファイルを作るのを目標にしています", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_テンプレートのファイルに対して音声とか画像を足して動画にできるファイルを作るのを目標にしています.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "そのまま動画にするとこまで作るのは間に合わんかったけどな", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_そのまま動画にするとこまで作るのは間に合わんかったけどな.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "締め切りは無慈悲でブルタルです", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_締め切りは無慈悲でブルタルです.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "次に現状と課題やで", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_次に現状と課題やで.wav"}) ({:isSuccess true, :libraryName "雫", :engineName "", :Body "絶賛制作中って感じのチェックリストですね", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_絶賛制作中って感じのチェックリストですね.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "せやな", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_せやな.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "先行研究で各種ttsを統一的に使えるライブラリがあったので大いに助かっています。", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_先行研究で各種ttsを統一的に使えるライブラリがあったので大いに助かっています。.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "進捗は…とりあえず来年1月ぐらいに動くのができるかもしれないです", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_進捗は…とりあえず来年1月ぐらいに動くのができるかもしれないです.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "その他補足としては、製作中aviutlで使えるファイルにするために手間取ったので…", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_その他補足としては、製作中aviutlで使えるファイルにするために手間取ったので….wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "pythonが使えるらしいDavinchi対応は優先してやりたいのですが、私はpythonわからないんですよね", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_pythonが使えるらしいDavinchi対応は優先してやりたいのですが、私はpythonわからないんですよね.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "やればたぶんできるで、たぶんな", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_やればたぶんできるで、たぶんな.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "あとは…この中だと調声できないってのは微妙やない？", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_あとは…この中だと調声できないってのは微妙やない？.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "そうなんですが、対応できるのが結構後になりそうです、UI作ってからになるので…", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_そうなんですが、対応できるのが結構後になりそうです、UI作ってからになるので….wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "1文ごとに順次読み上げをするのはやや楽なので、事前にフレーズ編集してもらえればいいかと思っています", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_1文ごとに順次読み上げをするのはやや楽なので、事前にフレーズ編集してもらえればいいかと思っています.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "まあ動いてからの話になるけどな", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_まあ動いてからの話になるけどな.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "そうですね…", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_そうですね….wav"}) ({:isSuccess true, :libraryName "茜", :engineName "", :Body "次は導入方法の予定だったけど、まだ導入できる状態でもないので現状はここで締めるで", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_次は導入方法の予定だったけど、まだ導入できる状態でもないので現状はここで締めるで.wav"} {:isSuccess true, :libraryName "雫", :engineName "", :Body "ご視聴ありがとうございました", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\雫_ご視聴ありがとうございました.wav"} {:isSuccess true, :libraryName "茜", :engineName "", :Body "ありがとなー", :outputPath "e:\\Documents\\descript-to-video\\output\\voices\\茜_ありがとなー.wav"})))
(def sample-template-map (parser/aviutl-object->yaml (slurp sample-template-path :encoding "shift-jis")))

(deftest descript-to-video.aviutl.aviutl-test
  (testing "get-templateのテスト"
    (testing "設定値がある場合"
      (is (=
           (get-in setting [:ゆかり :alias])
           (get-template "ゆかり"))))
    (testing "設定値がない(defaultを返す)場合"
      (is (=
           (get-in setting [:default :alias])
           (get-template "ぬかり")))))
  (testing "gen-objectのテスト")
  (testing "formatterのテスト"
    (is (formatter/padded-hex->string (formatter/string->padded-hex (str prefix "ゆかりさんでした" suffix))))))

(comment
  (run-tests)
  (require '[descript-to-video.aviutl.aviutl :refer :all] :reload)
  (slurp (get-template "ゆかり") :encoding "shift-jis")
  ;; slideの.exoで更新する内容の期待値
  (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")
  (spit "output/slide2.exo" (parser/yaml->aviutl-object (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")) :encoding "shift-jis")
  (m/deep-merge-with (fn [v1 v2] (println v1 v2) (cond (map? v2) (into (ordered-map) v2) :else v1)) '{:0 {:start "300" :end "600" :0.0 {:file "E:\\Videos\\VoiceroidWaveFiles.002.png"}}})
  (formatter/padded-hex->string (formatter/string->padded-hex (str prefix "ゆかりさんでした" suffix)))

  (def sample-map (parser/aviutl-object->yaml (slurp "./sample/sample.exo" :encoding "shift-jis")))
  (def raw-text (slurp sample-file-path))
  (def sample-slide-lines (mdparser/split-by-slides raw-text))
  (def tts-lines (map mdparser/get-voiceroid-text-lines (mdparser/split-by-slides raw-text)))
  ;; (def slide-pathes (marp/export-slides sample-file-path))
  ;; (= tts-results sample-tts-results)
  ;; (def tts-results (tts/record-lines tts-lines))
  
  ;; (spit
  ;;  "../test.exo"
  ;;  (parser/yaml->aviutl-object tts-joined) :encoding "shift-jis")
  ;; (def tts-objects (get-tts-objects (flatten tts-results)))
  (def end-positions (get-slide-display-positions sample-tts-results))
  ;; (def line-per-slides (map count sample-tts-results))
  (def tts-objects (get-tts-objects (flatten sample-tts-results)))
  
  (def slide-objects (get-slide-objects sample-slide-pathes end-positions))
  (vector? tts-objects)
  (vector? slide-objects)
  (last (concat slide-objects tts-objects))
  (map #(get-in % [:0 :start]) (concat tts-objects slide-objects))
  (def all-joined
    (-> sample-template-map
        (merge-aviutl-objects slide-objects)
        (merge-aviutl-objects tts-objects)))
  (spit "../sample_result.exo" (parser/yaml->aviutl-object all-joined) :encoding "shift-jis")
  
  (merge-aviutl-objects (parser/aviutl-object->yaml (slurp sample-template-path :encoding "shift-jis")) slide-objects)
  (reduce parser/concat-aviutl-map sample-template-map slide-objects)
  (rest  slide-objects)
  (reduce parser/concat-aviutl-map sample-template-map tts-objects)
  (spit "../slides.exo" (parser/yaml->aviutl-object (reduce parser/concat-aviutl-map slide-objects)) :encoding "shift-jis")
  


  (get-slide-object-as-ordered-map sample-file-path)
  (def sample-slides-objects
    ;; 以下スライドの表示要素に対する.exoをまとめて生成
    (for [serial (map inc (range (count sample-slide-lines)))
          :let [temp-file-path (str sample-file-path "." serial ".png")]]
      (get-slide-object-as-ordered-map temp-file-path)))
  (def sample-tts-objects
    ;; 以下スライドのtts内容に対する.exoをまとめて生成
    (for [lines sample-tts-lines]
      (reduce concat (map #(get-tts-object (str %) (first %) (rest %)) lines))))

  ;; slide-mergedとtts-mergedをテンプレにした.exoにマージすればとりあえず目標達成になるはず
  (def slide-merged
    (reduce
     parser/concat-aviutl-map
     sample-map
     sample-slides-objects))
  (def slide-and-tts-merged
    (reduce parser/concat-aviutl-map
            slide-merged
            sample-tts-objects))
  (->
   (parser/concat-aviutl-map sample-map (first (reduce concat sample-tts-objects)))
  ;;  keys
   parser/yaml->aviutl-object
   println)

  ;; slideに関してはうまくマージできてるっぽい
  (spit "../tmp_slides.exo" (parser/yaml->aviutl-object slide-merged) :encoding "shift-jis")
  ;; ttsもできた
  (spit "../tmp_slides-and-tts.exo" (parser/yaml->aviutl-object slide-and-tts-merged) :encoding "shift-jis")
  (spit "../tmp_slides-and-tts.yaml"  (yaml.writer/generate-string slide-and-tts-merged) :encoding "shift-jis")

  (def lib-text  (map mdparser/get-voiceroid-text-lines (mdparser/split-by-slides raw-text)))
  (def tts-results (tts/record-lines lib-text))
  (def template-path "E://Documents/descript-to-video/sample/sample.exo")
  (def tts-joined (get-tts-objects template-path  tts-results))
  (loop [start 1
         res  (flatten tts-results)
         result (parser/aviutl-object->yaml (slurp template-path :encoding "shift-jis"))]
    (println (empty? res))
    (cond (empty? res) result
          :else
          (let [ttsResult (first res)
                obj (get-tts-object start (:outputPath ttsResult) (:Body ttsResult) (:libraryName ttsResult))]
            (println (:outputPath ttsResult) ttsResult)
            (recur (dec (+ start (a/get-wav-length (:outputPath ttsResult))))
                   (rest res)
                   (parser/concat-aviutl-map
                    result
                    obj)))))
  (seq? tts-results)
  (rest tts-results)
  (first tts-results)
  (let [start 1
        ttsResult (first tts-results)
        result (parser/aviutl-object->yaml (slurp template-path :encoding "shift-jis"))]
    (println (dec (+ start (a/get-wav-length (:outputPath ttsResult)))))
    ;; (rest tts-results)
    (parser/concat-aviutl-map
     result
     (get-tts-object start (:outputPath ttsResult) (:Body ttsResult) (:libraryName ttsResult))))
  )