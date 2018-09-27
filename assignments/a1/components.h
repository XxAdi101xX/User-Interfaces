#ifndef __COMPONENTS_H__
#define __COMPONENTS_H__

#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <string>

class Displayable {
	public:
		virtual void paint(Display* display, Pixmap buffer) const = 0;
};

class Ball: public Displayable {
	const double startingXPosition;
	const double startingYPosition;
	double xPosition;
	double yPosition;
	double xDirection;
	double yDirection;
	const int ballSize;
	GC gc;
public:
	Ball(double xPos, double yPos, GC gc, int ballSize, double ballSpeed);
	virtual void paint(Display* display, Pixmap buffer) const;
	double xPos() const;
	double yPos() const;
	double xDir() const;
	double yDir() const;
	int size() const;
	void updatePos();
	void invertXDir();
	void invertYDir();
	void replaceGC(GC newGC);
	void reset();
};


class Block: public Displayable {
	int x;
	int y;
	GC gc;
	int currentHealth;
	const int maxHealth;
	const int widthDimension = 105;
	const int heightDimension = 50;
public:
	Block(int x, int y, GC gc, int health = 1);
	virtual void paint(Display* display, Pixmap buffer) const;
	int xPos() const;
	int yPos() const;
	int width() const;
	int height() const;
	bool isDestroyed() const;
	void onHit();
	void reset();
};


class Paddle: public Displayable {
  const int widthDimension = 150;
  const int heightDimension = 30;
  const int startingXPosition;
  const int startingYPosition;
	int x;
	int y;
	GC gc;
public:
	Paddle(int x, int y, GC gc);
	virtual void paint(Display* display, Pixmap buffer) const;
	int xPos() const;
	int yPos() const;
	int width() const;
	int height() const;
	void moveXPos(int offset, int windowWidth);
	void reset();
};

class Text: public Displayable {
	int x;
	int y;
	GC gc;
	std::string text;
public:
	Text(int x, int y, GC gc, std::string text);
	virtual void paint(Display* display, Pixmap buffer) const;
	void update(std::string newText);
	std::string getText() const;
};

#endif
