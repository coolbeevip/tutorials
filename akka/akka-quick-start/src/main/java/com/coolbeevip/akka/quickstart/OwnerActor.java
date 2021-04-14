package com.coolbeevip.akka.quickstart;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;


/**
 * 聊天室主人
 * @author zhanglei
 */
public class OwnerActor extends AbstractBehavior<VisitorActor.Greeted> {

    public static Behavior<VisitorActor.Greeted> create(int max) {
        return Behaviors.setup(context -> new OwnerActor(context, max));
    }

    private final int max;
    private int greetingCounter;

    private OwnerActor(ActorContext<VisitorActor.Greeted> context, int max) {
        super(context);
        this.max = max;
    }

    @Override
    public Receive<VisitorActor.Greeted> createReceive() {
        return newReceiveBuilder().onMessage(VisitorActor.Greeted.class, this::onGreeted).build();
    }

    private Behavior<VisitorActor.Greeted> onGreeted(VisitorActor.Greeted message) {
        greetingCounter++;
        getContext().getLog().info("Greeting {} for {}", greetingCounter, message.whom);
        if (greetingCounter == max) {
            return Behaviors.stopped();
        } else {
            message.from.tell(new VisitorActor.Greet(message.whom, getContext().getSelf()));
            return this;
        }
    }
}
