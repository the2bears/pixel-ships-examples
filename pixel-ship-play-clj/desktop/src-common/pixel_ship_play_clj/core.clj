(ns pixel-ship-play-clj.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [pixel-ships.core :as psc :refer :all]
            [pixel-ships.bollinger :as bollinger :refer :all]))

(declare create-pixel-ship-play-clj custom-shape play-clj-color hsv-to-rgb)

(defn create-entity [screen]
  (let [pixel-ship (bundle nil)]
    pixel-ship))

(defn create-pixel-ship-play-clj [ship-map]
  (let [tags (keys (:pixels ship-map))
        pixels (:pixels ship-map)
        hull ((comp :hull :pixels) ship-map)
        shape-builder (fn[s] (reduce (fn[acc n] (conj acc (custom-shape n))) [] s))]
    (reduce (fn[acc tag](concat (shape-builder (tag pixels)) acc)) [] tags)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen :renderer (stage))
          pixel-ship (create-entity screen)
          ship-map (psc/color-pixel-ship (psc/create-pixel-ship (assoc bollinger/model :seed (rand-int Integer/MAX_VALUE))))]
      [(assoc pixel-ship :entities (create-pixel-ship-play-clj ship-map)
         :x 200 :y 200 :angle 180)]
      ))

  :on-render
  (fn [screen entities]
    (clear! 0.9 0.9 0.9 1)
    (render! screen entities)))


(defgame pixel-ship-play-clj-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(-> main-screen :entities deref)

(def p-per-c 20)

(defn custom-shape [{:keys [x y color]}]
  (let [c (play-clj-color color)]
    (shape :filled :set-color c :rect (- (* x p-per-c) (* 6 p-per-c)) (- (* y p-per-c) (* 6 p-per-c)) p-per-c p-per-c)))

(defn play-clj-color
  ([{:keys [h s v]}]
   (let [[r g b a] (hsv-to-rgb [h s v 1])]
     (play-clj-color r g b a)))
  ([r g b a]
   (color r g b a)))

(defn hsv-to-rgb
  ;Convert hsv to rgb
  ;Inputs are floats 0<i<1
  ([[hue saturation value alpha]]
  (if (= saturation 0)
    [value value value alpha]
    (let [hue2 (cond (= 1.0 hue) 0.0
                     :else hue)
          h (int (* hue2 6.0))
          f (- (* hue2 6.0) h)
          p (* value (- 1 saturation))
          q (* value (- 1 (* f saturation)))
          t (* value (- 1 (* (- 1 f) saturation)))]
          (case h
            0 [value t p alpha]
            1 [q value p alpha]
            2 [p value t alpha]
            3 [p q value alpha]
            4 [t p value alpha]
            [value p q alpha])

    ))))

