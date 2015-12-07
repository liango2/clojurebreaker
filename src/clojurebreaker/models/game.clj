(ns clojurebreaker.models.game)

(defn create
  "生成一个密码"
  []
  (repeatedly 4 #(rand-nth ["r" "b" "g" "y"])))

(println (create))


"
在repl中测试:

len repl

(require :repload 'clojurebreaker.models.game)
(in-ns 'clojurebreaker.models.game)
(create)

"
;(dotimes [i 10] (println "第" i "次:" (create)))

(require '[clojure.data :as data])

(defn cnt-of-same-position
  "返回 相同位置 完全相同的元素的个数"
  [c1 c2]
  (let [[_ _ matches] (data/diff c1 c2)]
    (count (remove nil? matches))))

(cnt-of-same-position [:a :b :b :c] [:a :d :d :c])

(def array-secret [:a :b :b :c])
(frequencies array-secret)
; -> {:a 1, :b 2, :c 1}

(def array-guess [:d :d :d :b])
(frequencies array-guess)
; -> {:d 3, :b 1}

(select-keys (frequencies array-secret) array-guess)
; -> {:b 2}
(select-keys (frequencies array-guess) array-secret)
; -> {:b 1}

(merge-with min
            (select-keys (frequencies array-secret) array-guess)
            (select-keys (frequencies array-guess) array-secret))
; -> {:b 1}

(defn element-cnt-entries_of_guess-success_unordered
  "返回 猜中的元素和猜中的次数"
  [c1, c2]
  (let [m1 (select-keys (frequencies c1) c2),
        m2 (select-keys (frequencies c2) c1)]
    (merge-with min m1 m2)))
(element-cnt-entries_of_guess-success_unordered
  array-secret
  array-guess
  )
; -> {:b 1}


(element-cnt-entries_of_guess-success_unordered
  [:r :g :g :b]
  [:r :y :y :g])
; -> {:g 1, :r 1}


(vals
  (element-cnt-entries_of_guess-success_unordered
    [:r :g :g :b]
    [:r :y :y :g]))
; -> (1 1)

(defn score_exact-cnt_and_unordered-cnt
  "统计精确匹配的个数 与 无序匹配的个数"
  [c1 c2]
  (let [exact     (cnt-of-same-position                           c1 c2),
        unordered (apply + (vals (element-cnt-entries_of_guess-success_unordered c1 c2)))
        ]
    {:exact   exact,
     :unordered (- unordered exact)}
    )
  )

(println (score_exact-cnt_and_unordered-cnt
   [:r :g :g :b]
   [:r :y :y :g]))


