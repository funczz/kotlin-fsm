package com.github.funczz.kotlin.fsm

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * ステートとコンテキストを保持する有限ステートマシンクラス
 * @param <EV> イベントの型
 * @param <CTX> ステートマシンで処理するコンテキストの型
 * @author funczz
 * @version 1.0.0
 */
open class ReentrantLockedFsm<EVENT : Any, CTX : Any>(

    /** 初期ステート */
    state: FsmState<EVENT, CTX>,

    /** コンテキスト */
    context: CTX,

    ) {

    /** ロック */
    private val lock: ReentrantLock = ReentrantLock()

    /** ステート */
    private var _state: FsmState<EVENT, CTX> = state

    /** コンテキスト */
    private var _context: CTX = context

    /** ステートを返却する */
    val state: FsmState<EVENT, CTX>
        get() = _state

    /** コンテキストを返却する */
    val context: CTX
        get() = _context

    /**
     * イベントに応じて処理を実行して状態を遷移する
     *  @param event イベント
     */
    fun fire(event: EVENT) = lock.withLock {
        val result = _state.fire(event = event, context = _context)
        _state = result.first
        _context = result.second
    }

}