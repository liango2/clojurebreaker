(ns clojurebreaker.views.welcome
  (:require [clojurebreaker.views.common :as common]
            [clojurebreaker.models.game :as game]
            [noir.session :as session]
            [noir.content.getting-started])
  (:use
    [noir.core :only [defpartial defpage render]]
    [hiccup.page :only [include-css html5]]
    [hiccup.form]
    ))

(defpartial board [{:keys [one two three four exact unordered]}]
            [:h1 "这是一个web版的Cojure电码破解游戏"]
            [:p  "在这个游戏中, 程序创建了一个正序(N-orderd)的彩钉(r,g,b,y)密码.然后由人类玩家提交猜测的结果."]
            [:ul "程序 对 玩家猜测的结果计分规则如下:"]
            [:li "每当有一个颜色和位置都正确的彩钉, 就得到一枚黑色钉."]
            [:li "每当有一个颜色正确但位置有误的彩钉, 就得到一枚白色钉."]
            [:p  "全都猜对了, 或者是 猜错次数达到了上限, 游戏结束."]

            [:line]
            [:p "请输由(r,g,b,y)组成的任意组合:"]
            (form-to [:post "/guess"]
                     (text-field "one" one)
                     (text-field "two" two)
                     (text-field "three" three)
                     (text-field "four" four)
                     (submit-button "Guess"))

            [:line]
            [:p "得分:"]
            (when (and exact unordered)
              [:div "完全匹配的个数: " exact " ,无序但能匹配的个数:" unordered])

            )

(defpage "/" {:as guesses}
         (println "hello,world")
         (when-not (session/get :game)
           (session/put! :game (game/create)))
         #_(common/layout
             [:p "hello,1"]
             [:p "hello,2"]
             [:p "你当前的session中 :game的对应value是:" (session/get :game)])
         ;"bla bla bla"
         (board (or guesses nil)))

(defpage [:post "/guess"] {:keys [one two three four]}
         (let [result (game/score_exact-cnt_and_unordered-cnt [one two three four] (session/get :game))]
           (if (= 4 (:exact result))
             (do (session/remove! :game)
                 (common/layout [:h2 "恭喜你, 答对啦."]
                                (form-to [:get "/"]
                                         (submit-button "start a new game"))))
             (do (apply session/flash-put! result)
                 (render "/"
                         {:one       one
                          :two       two
                          :three     three
                          :four      four
                          :exact     (:exact result)
                          :unordered (:unordered result)})))))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to clojurebreaker"]))


(defpage "/my-page" []
         (common/layout
           [:h1 "This is my first page!"]
           [:p "Hope you like it."]))


(defpage "/my-page" []
         (common/layout
           [:h1 "This is my first page!"]
           [:p "Hope you like it."]))
