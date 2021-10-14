(ns descript-to-video.tts-test
  (:require
   [clojure.test :refer :all]
   [descript-to-video.tts :refer :all]))

(comment
  (def sample-texts '(["ゆかり" "これはサンプルのスライドです"] ["ゆかり" "解説はコメントに書くようにしています、"] ["ゆかり" "テキストを書いてコマンド実行すると、"] ["ゆかり" "コメントの中からvoiceroidで読み上げるテキストを拾って"] ["ゆかり" "読み上げが終わったら次のスライドを表示するようにしてくれます"] ["ゆかり" "biim風のレイアウトにもできます"]))
  (def talk-task (talk-agent "葵" "さとうささらです"))
  (def ag (agent "葵"))
  (send-off ag #(talk %1 %2)  "さとうささらです")
  (await-for 10000 ag)
  (await-for 10000 talk-task)
  (let [ag (talk-agent "茜" "茜ちゃんやで")]
    (await-for 10000 ag))
  (let [ag (save-to-file-agent "茜" "茜ちゃんやで")]
    (await-for 10000 ag))

  (talk "葵" "さとうささらです")
  (save-to-file "葵" "葵です")
  (save-to-file "茜" '("あかねちゃんやで" "葵です"))
  (save-to-file "さとうささら" "さとうささらです")
  (save-to-exo "ゆかり" "別のゆかりさんです" 1)
  (save-to-exo "ゆかり" "その他のゆかりさんです" 1)
  )