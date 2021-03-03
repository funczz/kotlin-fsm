package com.github.funczz.kotlin.fsm

/**
 * インターフェイス
 * ステートマシン
 * @param <T> ステートマシンで処理するコンテキストの型
 * @author funczz
 */
interface IFsm<T : Any> {

    /**
     * イベントが発火可能かを返す
     * @param eventType イベントの Class
     * @return 内部遷移/外部遷移/無視するイベントとして登録されている場合 <code>true</code> 、
     *         それ以外は <code>false</code>
     */
    fun canFire(eventType: Class<*>): Boolean

    /**
     * イベントを発火する
     * @param event イベントのインスタンス
     * @throws NotRegisteredEventFsmException 登録されていないイベントを発火した
     */
    fun fire(event: Any)

    /**
     * 現在の状態を取得する
     * @return <code>IFsmState<T></code>
     */
    fun state(): IFsmState<T>

    /**
     * 現在のコンテキストを取得する
     * @return <code>T</code>
     */
    fun context(): T

}