package com.wecast.player.di.component;

import com.wecast.core.di.component.CoreComponent;
import com.wecast.player.di.PlayerScope;
import com.wecast.player.di.module.PlayerModule;

import dagger.Component;

/**
 * Created by ageech@live.com
 */

@PlayerScope
@Component(
        modules = PlayerModule.class,
        dependencies = CoreComponent.class
)
public interface PlayerComponent {
}
