(ns descript-to-video.aviutl.parser
  (:require
   [clojure.string :as s]
   [yaml.core :as yaml]))

(defn indent
  "aviutlのobject形式のstringをyamlでparseできるようインデントする"
  [string]
  (loop [lines (s/split-lines string)
         nest ""
         result '[]]
    (if
     (nil? (first lines)) (s/join "\r\n" result)
     (let [line (first lines)
           match (re-matches #"\[\w+\]" line)]
       (cond
        ;;  [exedit]か[{レイヤー番号}]を含む行の場合
         (not= nil match) (recur (rest lines) "  " (conj result line))
        ;;  オプション定義の行の場合
         (s/includes? line "[") (recur (rest lines) "    " (conj result (str nest line)))
         ;; それ以外の場合、nestの値そのままでもう一度コール
         :else (recur (rest lines) nest (conj result (str nest line))))))))

(defn aviutl-obj->yaml
  "aviutlのobjectをyaml形式でparseする"
  [text]
  (->
   text
   (indent)
   (s/replace "[" "\"")
   (s/replace "]" "\": ")
   (s/replace "=" ": ")
   (yaml/parse-string)))

(defn get-level
  "aviutlのオブジェクトについて、ネストの階層を返す"
  [current keys]
  (cond
    (nil? current) nil
    (string? current)
      ;; aviutlのobject:"[{オブジェクトの通し番号}\.{エフェクトの通し番号}]"
    (let [[_ layerSerialNo optionSerialNo] (re-matches #"\[(\w+)\.*(\w+)?\]" current)]
      (cond (nil? layerSerialNo) keys
            (nil? optionSerialNo) (vector (keyword layerSerialNo))
            :else (vector (keyword layerSerialNo) (keyword (str layerSerialNo "." optionSerialNo)))))
    :else keys))

(defn get-keyval
  [st]
  (cond (not (string? st)) nil
        :else (let [[key val] (s/split st #"=")]
                (println key val)
                {(keyword key) (str val)})))

(defn aviutl-obj->yaml2 [text]
  (loop [lines (s/split-lines text)
         nest '[]
         result '[]]
    ;; (println nest (get-level (first lines) nest) (map #(contains? (set nest) %) (get-level (first lines) nest)))
    (cond
      (empty? lines) result
      :else (let [line (first lines)
                  lv (get-level line nest)
                    ;; 現在参照している行に対するインデント
                  indent (cond
                            ;;  前の行と現在の行がemptyであれば0(エラー回避)
                           (or (empty? lv) (empty? nest)) 0
                            ;; 前の行と現在の行が一致している場合、keyの数がそのまま階層
                           (= lv nest) (count lv)
                            ;;  それ以外の場合、現在の行のキーのリストのうち、前の行のキーのリストと重複のキーの個数
                           :else (count (filter true? (map #(contains? (set nest) %) lv))))]
              (recur (rest lines) lv (conj result (str (s/join "" (repeat indent "  ")) line)))))))

(defn format-key [symbol]
  (str "[" (name symbol) "]"))

(defn format-level [key val]
  (str (name key) "=" val))

(defn get-nested-keys
  "ネストしたmapのkeyをvectorとして返す"
  [mp result]
  (loop [todos (map list (keys mp))
         temp result]
    (let [first-keys (first todos)
          val (get-in mp  first-keys)]
      (cond
        (empty?  todos) temp
        (map? val) (recur
                    (concat (map #(flatten (list first-keys %)) (keys val)) (rest todos))
                    (conj temp first-keys))
        :else (recur
               (rest todos)
               (conj temp first-keys))))))

(defn yaml->aviutl-object
  [mp]
  (letfn [(format-keyval
            [mp keys]
            (let [val (get-in mp keys)]
              (cond (map? val) (format-key (last keys))
                    :else (format-level (last keys) val))))]
    (s/join "\r\n" (reduce #(conj %1 (format-keyval mp %2)) '[] (get-nested-keys mp '[])))))

(comment
  ;; 動作確認に使っているスニペット系
  ;; TODO:テストに移す
  (def raw-object (slurp "./sample/sample.exo" :encoding "shift-jis"))
  (def sample-yaml (yaml/from-file "./sample/sample.yaml"))
  (def parsed  (aviutl-obj->yaml raw-object))
  (get-nested-keys (:0.1 (:0 sample-yaml)) '())
  (get-nested-keys (:1 sample-yaml) '[])
  (get-nested-keys sample-yaml '[])
  (spit "../tmp.txt" (yaml->aviutl-object sample-yaml) :encoding "shift-jis")

  (map list (keys parsed))
  (map #(list :exedit %) (keys (get-in parsed '(:exedit))))
  (map #(get-in sample-yaml %) (map #(list :exedit %) (keys (get-in parsed '(:exedit)))))
  (reverse (cons (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))
  (reverse (concat (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))


  (spit "../tmp2.txt" (s/join "\r\n" (aviutl-obj->yaml2 raw-object)) :encoding "shift-jis")
    ;; (reduce #(assoc-in %1 (get-level %2 %2) (get-keyval %2)) (s/split-lines lines)))
  (assoc-in '{} (get-level "[0]" '[]) (get-keyval "start=1"))
  (get-keyval "_name=標準描画")
  (get-keyval "拡大率=100.00")
  (get-keyval "color2=000000")
  (repeat 1 "hoge")
  (let [st "_name=標準描画"
        [key val] (s/split st #"=")]
    (println key val))
  (get-level nil '[])
  (map #(contains? (set '[:0 :0.1]) %) '[:0.1])
  (map #(contains? (set '[:0 :0.1]) %) '[:0])
  (map #(contains? (set '[:exedit]) %) '[:0])
  (map #(contains? (set '[:0 :0.1]) %) '[:0])
  (count (filter true? (map #(contains? (set '[:0 :0.1]) %) '[:0 :0.2])))
  (filter #(= :0 %) '[:0 :0.1])
  (keep #(contains? '[:0] %) '[:0])
  (filter #(contains? '[:0] %) '[:0 :0.1])
  (:0 '[:0 :0.1])
  (first '[:0 :0.1])

  (get-level "hoge" '[])
  (count (get-level "hoge" '[:0]))
  (get-level "[1]" '[:0])
  (get-level "[1.41]" '[:0])
  (get-level "_name=標準描画" '[:0]))