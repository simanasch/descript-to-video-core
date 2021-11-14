(ns descript-to-video.tts.tts-test
  (:require
   [clojure.test :refer :all]
   [descript-to-video.tts.tts :refer :all]))

(comment
  (def raw-text (slurp "sample/sample.md"))
  (def sample-lines 
    (map 
     descript-to-video.markdown.parser/get-voiceroid-text-lines 
     (descript-to-video.markdown.parser/split-by-slides raw-text)))

  (talk "葵" "さとうささらです")
  ;; todo? save-to-exoで出力した.exoファイルがなんかバグってる(prefix,suffixが二重についてた)
  (save-to-exo "雫" "ちょっと別撮りが入りますが、実際動かしてみるとこんな感じになります" 1)
  (save-to-exo "雫" "テンプレートにする.exoファイルと、マークダウンのファイルを選択してボタンを押すと録音開始みたいな感じです" 1)
  
  (save-to-file "葵" "voiceroidで保存のテストだよ")
  (save-to-file "茜" '("あかねちゃんやで" "葵です"))
  (save-to-file "さとうささら" "さとうささらです")
  (save-to-exo "ゆかり" "別のゆかりさんです" 1)
  (save-to-exo "ゆかり" "その他のゆかりさんです" 1)
  (talk-lines sample-lines)
  (record-lines sample-lines)
  )