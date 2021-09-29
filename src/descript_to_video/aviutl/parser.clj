(ns descript-to-video.aviutl.parser
  (:require
   [clojure.string :as s]
   [yaml.core :as yaml]))

(defn- get-level
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

(defn- get-keyval
  [st]
  (cond (not (string? st)) nil
        :else (let [[key val] (s/split st #"=")]
                (println key val)
                {(keyword key) (str val)})))

(defn- format-aviutl-line-to-yaml
  "aviutlの.objの行に対して、yamlのタグとして読めるよう整形する"
  [text]
  (->
   text
  ;;  パスのエスケープ
   (s/escape {\" "\\\"", \\ "\\\\"})
  ;;  objectの通し番号の整形
   (s/replace "[" "\"")
   (s/replace "]" "\": ")
  ;;  設定値の整形、値をダブルクォートで囲む
   (s/replace #"=(.*)" ": \"$1\"")))

(defn format-aviutl-object->yaml
  "aviutlの.obj形式のテキストをyamlとして読める形式のテキストにして返す"
  [text]
  (loop [lines (s/split-lines text)
         nest '[]
         result '[]]
    (cond
      (empty? lines) (s/join "\r\n" result)
      :else
      (let [line (first lines)
            lv (get-level line nest)
            indent (cond
                     (or (empty? lv) (empty? nest)) 0
                     (= lv nest) (count lv)
                     :else (count (filter true? (map #(contains? (set nest) %) lv))))]
        (recur
         (rest lines)
         lv
         (conj result (str (s/join "" (repeat indent "  ")) (format-aviutl-line-to-yaml line))))))))

(defn aviutl-object->yaml
  "aviutlの.obj形式のテキストをyamlに変換し,yamlとしてパースした結果のordered-mapを返す"
  [text]
  (yaml/parse-string (format-aviutl-object->yaml text)))

(defn- format-key [symbol]
  (str "[" (name symbol) "]"))

(defn- format-level [key val]
  (str (name key) "=" val))

(defn- get-nested-keys
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
  "yamlのmapをaviutlのobject形式にして返す"
  [mp]
  (letfn [(format-keyval
            [mp keys]
            (let [val (get-in mp keys)]
              (cond (map? val) (format-key (last keys))
                    :else (format-level (last keys) val))))]
    (s/join "\r\n" (reduce #(conj %1 (format-keyval mp %2)) '[] (get-nested-keys mp '[])))))

(defn conj-aviutl-map
  "yamlになっているaviutlのmap2つを結合する"
  [base add]
  (loop [key (inc (Integer/parseInt (name (last (keys base)))))
         keys-to-add (keys add)
         result base]
    ;; (println key keys-to-add)
    (cond (empty? keys-to-add) result
          :else
          (recur
           (inc key)
           (rest keys-to-add)
          ;;  オブジェクトの通し番号とオプションに含まれる通し番号が一致しないので要成形
           (assoc result (keyword (str key)) (get add (first keys-to-add)))))))

(defn sort-aviutl-object-keys
  "aviutlのobjectのmapについて、layerの昇順-startの昇順でソートする"
  [aviutl-object-map]
  (letfn [(sort-by-layer-start
            [x y]
            (let [layer-x (get-in aviutl-object-map [x :layer])
                  layer-y (get-in aviutl-object-map [y :layer])]
              (println "comparing:" x y layer-x layer-y)
              (cond (= layer-x layer-y) (compare (get-in aviutl-object-map [x :start]) (get-in aviutl-object-map [y :start]))
                    :else (compare (get-in aviutl-object-map [x :layer]) (get-in aviutl-object-map [y :layer])))))]
    (sort
     sort-by-layer-start
     (keys aviutl-object-map))))

(comment
  ;; 動作確認に使っているスニペット系
  ;; TODO:テストに移す
  (def sample-map (aviutl-object->yaml (slurp "./sample/sample.exo" :encoding "shift-jis")))
  (def sample-slide1 (aviutl-object->yaml (slurp "./sample/slide_template.exo" :encoding "shift-jis")))
  (def sample-slide2 (aviutl-object->yaml (slurp "./output/slide2.exo" :encoding "shift-jis")))
  (def raw-object (slurp "./sample/sample.exo" :encoding "shift-jis"))
  ;; sample-yamlとparsedは一致する(はず)
  (def raw-dest (slurp "./sample/sample_dest.exo" :encoding "shift-jis"))
  (def sample-yaml (yaml/from-file "./sample/sample.yaml"))
  (def parsed2 (aviutl-object->yaml raw-dest))
  (= sample-yaml sample-map)
  (spit "../tmp.txt" (yaml->aviutl-object parsed2) :encoding "shift-jis")
  ;; get-nested-keysのテスト
  (get-nested-keys (:0.1 (:0 sample-yaml)) '())
  (get-nested-keys (:1 sample-yaml) '[])
  (get-nested-keys sample-yaml '[])

  (map list (keys sample-map))
  (map #(list :exedit %) (keys (get-in sample-map '(:exedit))))
  (map #(get-in sample-yaml %) (map #(list :exedit %) (keys (get-in sample-map '(:exedit)))))
  (reverse (cons (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))
  (reverse (concat (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))

  (println (s/join "\r\n" (aviutl-object->yaml raw-object)))
  (yaml/parse-string (s/join "\r\n" (aviutl-object->yaml raw-object)))
  (format-aviutl-line-to-yaml "param=")
  (get-keyval "color2=000000")
  (repeat 1 "hoge")
  (s/replace "_name=標準描画"  #"=(.+)" ": \"$1\"")
  (def added (conj-aviutl-map sample-map sample-slide1))
  (keyword (str 2))
  (keys added)
  (get-in added [:0 :layer])
  (get-in added [:1 :layer])
  (Integer/parseInt (get-in added [:2 :layer]))
  (apply #(Integer/parseInt %) "121")
  (def sorted (sort-by (juxt :layer :start) (dissoc added :exedit)))
  (first   sorted)
  (println sorted)
  (seq? sorted)
  (map (fn [key] {key (get-in added [key :layer])}) (keys added))
  (sort-by
   (fn
     [key]
     (let [layer (get-in added [key :layer])
           start (get-in added [key :start])]
      ;;  (println key layer start)
       layer))
   (keys added))
  (sort-aviutl-object-keys added)

  (spit "./output/sorted.exo" (yaml->aviutl-object added) :encoding "shift-jis")
  (keys sorted))