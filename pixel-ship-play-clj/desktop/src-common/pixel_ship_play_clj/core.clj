(ns pixel-ship-play-clj.core
  (:require [play-clj.core :refer [bundle clear! color defgame defscreen key-code pixmap*
                                   pixmap! pixmap-format render! set-screen! shape stage update!]]
            [play-clj.g2d :refer [texture]]
            [pixel-ships.core :as psc :refer [create-pixel-ship color-pixel-ship]]
            [pixel-ships.bollinger :as bollinger :refer [model]]))

(declare create-pixel-ship-play-clj custom-shape play-clj-color hsv-to-rgb change-ship draw-rect-pixelmap)

(defn create-entity [screen]
  (let [pixel-ship (bundle nil)]
    pixel-ship))

(defn create-pixel-ship-play-clj
  ([]
   (create-pixel-ship-play-clj (rand-int Integer/MAX_VALUE)))
  ([seed]
   (let [ship-map (psc/color-pixel-ship (psc/create-pixel-ship (assoc bollinger/model :seed seed)))
         tags (keys (:pixels ship-map))
         pixels (:pixels ship-map)
         shape-builder (fn[s] (reduce (fn[acc n] (conj acc (custom-shape n))) [] s))]
     (reduce (fn[acc tag](concat (shape-builder (tag pixels)) acc)) [] tags))))

(defn create-pixel-map-list
  ([]
   (create-pixel-map-list (rand-int Integer/MAX_VALUE)))
  ([seed]
   (let [ship-map (psc/color-pixel-ship (psc/create-pixel-ship (assoc bollinger/model :seed seed)))
         tags (keys (:pixels ship-map))
         pixels (:pixels ship-map)
         shape-builder (fn[s] (reduce (fn[acc n] (conj acc n)) [] s))]
     (reduce (fn[acc tag](concat (shape-builder (tag pixels)) acc)) [] tags))))

(defn create-pixel-ship-texture
  ([]
   (create-pixel-ship-texture (rand-int Integer/MAX_VALUE)))
  ([seed]
   (let [pixel-map-list (create-pixel-map-list seed)
         pix-map (pixmap* 16 16 (pixmap-format :r-g-b-a8888))]
     (doseq [pixel pixel-map-list] (draw-rect-pixelmap pix-map pixel))
     (texture pix-map))))

(defn draw-rect-pixelmap [pix-map {:keys [x y color]}]
  (let [c (play-clj-color color)]
    (doto pix-map
      (pixmap! :set-color c)
      (pixmap! :fill-rectangle x y 1 1))))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen :renderer (stage))
          pixel-ship (create-entity screen)
          pixel-ship-texture (create-pixel-ship-texture Integer/MAX_VALUE)]
      [(assoc pixel-ship :entities (create-pixel-ship-play-clj Integer/MAX_VALUE)
         :id :pixel-ship :ship? true :x 200 :y 200 :angle 180)
       (assoc pixel-ship-texture :x 400 :y 400 :width 64 :height 64
         :id :pixel-ship-texture :ship-texture? true)]
      ))

  :on-render
  (fn [screen entities]
    (clear! 0.9 0.9 0.9 1)
    (render! screen entities))

  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :space))
      (change-ship screen entities)
      )))


(defgame pixel-ship-play-clj-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(defn change-ship [screen entities]
  (let [seed (rand-int Integer/MAX_VALUE)]
    (->> entities
         (map (fn [entity]
                (cond (:ship? entity)
                      (assoc entity :entities (create-pixel-ship-play-clj seed))
                      (:ship-texture? entity)
                      (assoc (create-pixel-ship-texture seed) :x 400 :y 400 :width 64 :height 64
                        :id :pixel-ship-texture :ship-texture? true)
                )))

         )))

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

(-> main-screen :entities deref)

;(use 'pixel-ship-play-clj.core.desktop-launcher)

;(-main)
