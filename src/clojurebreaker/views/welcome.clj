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
            [:div "1.-----------------------------------------"]
            (when (and exact unordered)
              [:div "完全匹配的个数: " exact " ,无序但能匹配的个数:" unordered])
            [:div "2.-----------------------------------------"]
            (form-to [:post "/guess"]
                     (text-field "one" one)
                     (text-field "two" two)
                     (text-field "three" three)
                     (text-field "four" four)
                     (submit-button "Guess")))

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
