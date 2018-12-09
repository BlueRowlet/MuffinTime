package com.figer.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.figer.game.bluetooth.BluetoothPermissions;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		BluetoothPermissions.verifyLocationPermissions(this);
		initialize(new GameMain(this.getContext()), config);
	}
}
