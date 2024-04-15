package kernel.io;

import geometry.*;

public class ScreenSaver {
  Rect bouncer;
  Vec2 vel;
  int width, height;

  public ScreenSaver() {
    bouncer = new Rect();
    bouncer.x = 0;
    bouncer.y = 0;
    bouncer.w = 30;
    bouncer.h = 20;

    vel = new Vec2();
    vel.x = 1;
    vel.y = 1;
  }

  public void update() {
    bouncer.x += vel.x;
    bouncer.y += vel.y;

    // if (bouncer.getBottom() > height) {
    //   bouncer.y = height - bouncer.h;
    //   vel.y *= -1;
    // }

    // if (bouncer.getRight() > width) {
    //   bouncer.x = width - bouncer.w;
    //   vel.x *= -1;
    // }

    // if (bouncer.getTop() < 0) {
    //   bouncer.y = 0;
    //   vel.y *= -1;
    // }

    // if (bouncer.getLeft() < 0) {
    //   bouncer.x = 0;
    //   vel.y *= -1;
    // }
  }

  public void draw() {
    Graphics.clear((byte)0x40);
    Graphics.drawRect(bouncer.x, bouncer.y, bouncer.w, bouncer.h, (byte)0x30);
  }
}