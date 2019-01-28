package com.wecast.player.di.wrapper;

import android.app.Application;

import com.wecast.core.di.component.CoreComponent;
import com.wecast.core.di.wrapper.CoreComponentWrapper;
import com.wecast.player.di.component.DaggerPlayerComponent;
import com.wecast.player.di.component.PlayerComponent;

/**
 * Created by ageech@live.com
 */

public class PlayerComponentWrapper {

    private static PlayerComponentWrapper componentWrapper;

    private PlayerComponentWrapper() {

    }

    private static PlayerComponentWrapper getInstance(Application application) {
        if (componentWrapper == null) {
            synchronized (PlayerComponentWrapper.class) {
                if (componentWrapper == null) {
                    componentWrapper = new PlayerComponentWrapper();
                    componentWrapper.initializeComponent(CoreComponentWrapper.getBaseComponent(application));
                }
            }
        }
        return componentWrapper;
    }

    private PlayerComponent component;

    public static PlayerComponent getAppComponent(Application application) {
        PlayerComponentWrapper appComponentWrapper = getInstance(application);
        return appComponentWrapper.component;
    }

    private void initializeComponent(CoreComponent coreComponent) {
        component = DaggerPlayerComponent.builder()
                .coreComponent(coreComponent)
                .build();
    }
}
