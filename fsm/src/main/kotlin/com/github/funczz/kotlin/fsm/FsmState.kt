package com.github.funczz.kotlin.fsm

/**
 * ステートを定義する
 * @param <EV> イベントの型
 * @param <CTX> ステートマシンで処理するコンテキストの型
 * @author funczz
 * @version 1.0.0
 */
interface FsmState<EVENT : Any, CTX : Any> {

    /**
     * イベントに応じて処理を実行して状態を遷移する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return 遷移後のステートとコンテキスト
     */
    fun fire(event: EVENT, context: CTX): Pair<FsmState<EVENT, CTX>, CTX> {
        return when (val nextState = getNextState(event = event, context = context)) {
            is NextState.Deny -> {
                throw IllegalArgumentException(
                    "Event not allowed: CurrentState=$this, Event=$event"
                )
            }

            is NextState.Ignore -> {
                val c = ignore(event = event, context = context)
                Pair(this, c)
            }

            is NextState.Internal -> {
                val c = internal(event = event, context = context)
                Pair(this, c)
            }

            is NextState.External -> try {
                @Suppress("UNCHECKED_CAST")
                val s = nextState.state as FsmState<EVENT, CTX>
                val c = external(state = s, event = event, context = context)
                Pair(s, c)
            } catch (e: ClassCastException) {
                throw IllegalArgumentException(
                    "Next state cannot be cast to a state object: CurrentState=$this, Event=$event",
                    e
                )
            }
        }
    }

    /**
     *  イベントを無視する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return コンテキスト
     */
    private fun ignore(@Suppress("UNUSED_PARAMETER") event: EVENT, context: CTX): CTX {
        return context
    }

    /**
     *  内部遷移で処理を実行する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return コンテキスト
     */
    private fun internal(event: EVENT, context: CTX): CTX {
        return onDo(event = event, context = context)
    }

    /**
     *  外部遷移で処理を実行する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return コンテキスト
     */
    private fun external(state: FsmState<EVENT, CTX>, event: EVENT, context: CTX): CTX {
        var c = this.onExit(event = event, context = context)
        c = state.onEntry(event = event, context = c)
        c = state.onDo(event = event, context = c)
        return c
    }

    /**
     *  イベントとコンテキストに応じて次の遷移先ステートを返却する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return 次の遷移先ステート
     */
    fun getNextState(event: EVENT, context: CTX): NextState

    /**
     *  このステートに外部遷移した時に、イベントとコンテキストに応じて処理を実行する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return コンテキスト
     */
    fun onEntry(event: EVENT, context: CTX): CTX

    /**
     *  このステートに遷移した時に、イベントとコンテキストに応じて処理を実行する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return コンテキスト
     */
    fun onDo(event: EVENT, context: CTX): CTX

    /**
     *  このステートから外部遷移する時に、イベントとコンテキストに応じて処理を実行する
     *  @param event イベント
     *  @param context コンテキスト
     *  @return コンテキスト
     */
    fun onExit(event: EVENT, context: CTX): CTX

}