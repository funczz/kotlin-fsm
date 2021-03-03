package com.github.funczz.kotlin.fsm

/**
 * インターフェイス
 * ステートマシンの持ち得る状態
 * @param <T> ステートマシンで処理するコンテキストの型
 * @author funczz
 */
interface IFsmState<T : Any> {

    /**
     * ENTRY アクションとしてファンクションを代入する
     * @param function イベント、コンテキストを引数とするファンクション
     */
    fun onEntry(function: (event: Any, context: T) -> Unit)

    /**
     * DO アクションとしてファンクションを代入する
     * @param function イベント、コンテキストを引数とするファンクション
     */
    fun onDo(function: (event: Any, context: T) -> Unit)

    /**
     * EXIT アクションとしてファンクションを代入する
     * @param function イベント、コンテキストを引数とするファンクション
     */
    fun onExit(function: (event: Any, context: T) -> Unit)

    /**
     * ENTRY アクションを呼び出す
     * @param event イベント
     * @param context コンテキスト
     */
    fun onEntry(event: Any, context: T)

    /**
     * DO アクションを呼び出す
     * @param event イベント
     * @param context コンテキスト
     */
    fun onDo(event: Any, context: T)

    /**
     * EXIT アクションを呼び出す
     * @param event イベント
     * @param context コンテキスト
     */
    fun onExit(event: Any, context: T)

    /**
     * 内部遷移するイベントを登録する
     * @param eventType イベントの Class
     */
    fun permit(eventType: Class<*>)

    /**
     * 外部遷移するイベントを登録する
     * @param eventType イベントの Class
     * @param state 遷移先の状態
     */
    fun permit(eventType: Class<*>, state: IFsmState<T>)

    /**
     * 無視するイベントを登録する
     * @param eventType イベントの Class
     */
    fun ignore(eventType: Class<*>)

    /**
     * イベントが発火可能かを返す
     * @param eventType イベントの Class
     * @return 内部遷移/外部遷移/無視するイベントとして登録されている場合 <code>true</code> 、
     *         それ以外は <code>false</code>
     */
    fun canFire(eventType: Class<*>): Boolean

    /**
     * イベントが無視されるかを返す
     * @param eventType イベントの Class
     * @return 無視するイベントとして登録されている場合 <code>true</code> 、
     *         それ以外は <code>false</code>
     */
    fun isIgnoreFire(eventType: Class<*>): Boolean

    /**
     * イベントを発火する
     * @param event イベントのインスタンス
     * @return 外部遷移するイベントの場合は遷移先の状態、
     *         それ以外は <code>null</code>
     */
    fun fire(event: Any): IFsmState<T>?

}