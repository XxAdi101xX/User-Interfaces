// CS 349 Fall 2018

#include <cstdlib>
#include <iostream>
#include <vector>
#include <string>
#include <unistd.h>
#include <sys/time.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>

#include "components.h"

using namespace std;

typedef struct {
  Block block;
  bool ballLeftOfBlock;
  bool ballRightOfBlock;
  bool ballAboveBlock;
  bool ballBelowBlock;
} BlockInfo;

// X11 main structures
Display* display;
Window window;
Pixmap buffer;

// fixed frames per second animation
int FPS = 60;

// ball speed
double ballSpeed = 4.0;

// window size configuration
const int windowWidth = 1280;
const int windowHeight = 800;

// get current time
unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_usec;
}

// handle bad command line args
void handleInvalidCmdArgs() {
	cerr << "Invalid Arguements!" << endl
			 <<	"Usage: './breakout [FPS] [BallSpeed]' where " 
			 << "10 <= FPS <= 60 and 1 <= BallSpeed <= 10" << endl;
	exit( EXIT_FAILURE );
}

// reset the game state
void resetGameState(vector<BlockInfo> &blocks, Ball &ball, Paddle &paddle, Text &currentScore) {
	for (auto &blockinfo : blocks) {
  	blockinfo.block.reset();
  }
  ball.reset();
	paddle.reset();
  currentScore.update("0");
}

// create gcs with some preset properties
GC createGC(XColor colour) {
	GC gc = XCreateGC(display, window, 0, 0);

  XSetFillStyle(display, gc, FillSolid);
  XSetForeground(display, gc, colour.pixel);
	return gc;
}

// entry point
int main( int argc, char *argv[] ) {
	// handle command line arguemetns
	if (argc == 3) {
		try {
			int inputFPS = stoi(argv[1]);
			double inputBallSpeed = stoi(argv[2]);
			double scalingRatio = 60.0 / inputFPS;

			if (inputFPS > 60 || inputFPS < 10 || inputBallSpeed > 10 || inputBallSpeed < 1) {
				throw 1;
			}

			// scale ballspeed according to inputFPS
			inputBallSpeed = inputBallSpeed * scalingRatio;

			FPS = inputFPS;
			ballSpeed = max(0.6 * inputBallSpeed, 1.0);
			cout << ballSpeed << endl;
		} catch (...) {
			handleInvalidCmdArgs();		
		}
	} else if (argc == 1) {
		cout << "Your game will start with a preset FPS of 60 and enjoyable ballspeed. Have fun!!!" << endl;
	} else {
		handleInvalidCmdArgs();
	}

	// create window
	display = XOpenDisplay("");
	if (display == NULL) exit (-1);
	int screennum = DefaultScreen(display);
	long background = WhitePixel(display, screennum);
	long foreground = BlackPixel(display, screennum);
	window = XCreateSimpleWindow(display, DefaultRootWindow(display),
                            10, 10, windowWidth, windowHeight, 2, foreground, background);

	// make window unresizable
	XSizeHints *hints = XAllocSizeHints();
	hints->min_width = windowWidth;
	hints->min_height = windowHeight;
	hints->max_width = windowWidth;
	hints->max_height = windowHeight;
	hints->width_inc = 0;
	hints->height_inc = 0;
	hints->flags = PAllHints;	
	XSetWMNormalHints(display, window, hints);

	// set events to monitor and display window
	XSelectInput(display, window, ButtonPressMask | KeyPressMask);
	XMapRaised(display, window);
	XFlush(display);

	// allocate colours
	Colormap screen_colormap = DefaultColormap(display, DefaultScreen(display));
  XColor red, brown, blue, yellow, green, black, white;

  XAllocNamedColor(display, screen_colormap, "red", &red, &red);
  XAllocNamedColor(display, screen_colormap, "brown", &brown, &brown);
  XAllocNamedColor(display, screen_colormap, "blue", &blue, &blue);
  XAllocNamedColor(display, screen_colormap, "yellow", &yellow, &yellow);
  XAllocNamedColor(display, screen_colormap, "green", &green, &green);
	XAllocNamedColor(display, screen_colormap, "black", &black, &black);
	XAllocNamedColor(display, screen_colormap, "white", &white, &white);

	// create gcs for images
  GC gcBlack = createGC(black);
	GC gcWhite = createGC(white);
	GC gcRed = createGC(red);
	GC gcBrown = createGC(brown);
	GC gcBlue = createGC(blue);
	GC gcYellow = createGC(yellow);
	GC gcGreen = createGC(green);	

	// initialize ball
	Ball ball(windowWidth / 2, windowHeight * 0.75, gcBlack, 35, ballSpeed);

	// initialize blocks
	vector<GC> gcs {gcRed, gcBrown, gcBlue, gcYellow, gcGreen};
	vector<BlockInfo> blocks;
	int offSet = 35;
	for (int i = 0; i < 5; ++i) {
		for (int j = 0; j < 11; ++j) {
			Block block(j * 110 + offSet, i * 55, gcs[i], 1);
			BlockInfo info{block, false, false, false, false};
			blocks.emplace_back(info);
		}
	}

	// initialize	paddle
	Paddle paddle((windowWidth / 2) - 75, windowHeight - 100, gcBlack);
	bool ballLeftOfPaddle = false;
  bool ballRightOfPaddle = false;
  bool ballAbovePaddle = false;
  bool ballBelowPaddle = false;

	// create buffer for double buffering
  int depth = DefaultDepth(display, DefaultScreen(display));
  buffer = XCreatePixmap(display, window, windowWidth, windowHeight, depth);

	// save time of last window paint
	unsigned long lastRepaint = 0;

	// event handle for current event
	XEvent event;

	// track hit score
	Text scoreTitle(1150, 775, gcRed, "Current Score: ");
	Text currentScore(1250, 775, gcRed, "0");

	// intro text
	string msg0 = "Welcome to Breakout!";
	string msg1 = "This game is created by Adithya Venkatarao (ID: a32venka && Student#: 20606942)";
	string msg2 = "To play this game, use the 'a' and 'd' buttons to move the paddle left and right respectively.";
	string msg3 = "You can press 'q' at anytime to quit the game. Press 'r' to start playing!!!";
	
	Text introMessage0(windowWidth / 2 - 120, 250, gcWhite, msg0);
	Text introMessage1(windowWidth / 4, 300, gcWhite, msg1);
	Text introMessage2(windowWidth / 4, 350, gcWhite, msg2);
	Text introMessage3(windowWidth / 4, 400, gcWhite, msg3);

	// end game text
	Text endGameMessage(windowWidth / 3, 525, gcRed, "");

	//bool firstEntry = true;
	bool intro = true;
	bool gameInPlay = false;

	// our main event loop!
	while ( true ) {
		if (XPending(display) > 0) { // if we have any events to handle
			XNextEvent( display, &event ); 

			switch ( event.type ) {
				case KeyPress: // any keypress
					KeySym key;
					char text[10];
					int i = XLookupString( (XKeyEvent*)&event, text, 10, &key, 0 );

					// move right
					if ( i == 1 && text[0] == 'd' && gameInPlay ) {
						paddle.moveXPos(15);
					}

					// move left
					if ( i == 1 && text[0] == 'a' && gameInPlay ) {
						paddle.moveXPos(-15);
					}

					// quit game
					if ( i == 1 && text[0] == 'q' ) {
						XCloseDisplay(display);
						exit(0);
					}

					// start game
          if ( i == 1 && text[0] == 'r' && !gameInPlay ) {
						resetGameState(blocks, ball, paddle, currentScore);
						gameInPlay = true;
						intro = false;
          }
					break;
				}
		}

		unsigned long end = now();	// get current time in microsecond

		if (end - lastRepaint > 1000000 / FPS) {
			// check if we have first loaded the game
			if (intro) {
				introMessage0.paint(display, buffer);
				introMessage1.paint(display, buffer);
				introMessage2.paint(display, buffer);
				introMessage3.paint(display, buffer);
			}
			
			// drawing our components depending on whether the game is active
			if (!gameInPlay) {
				endGameMessage.paint(display, buffer);
			} else {
				// clear background
      	XFillRectangle(display, buffer, gcWhite,
                     	0, 0, windowWidth, windowHeight);

				// draw paddle
				paddle.paint(display, buffer);

				// draw blocks
				for (auto &blockinfo : blocks) {
					if (!blockinfo.block.isDestroyed()) {
						blockinfo.block.paint(display, buffer);
					}
				}

				// draw ball from centre
				ball.paint(display, buffer);

				// show score
				scoreTitle.paint(display, buffer);
      	currentScore.paint(display, buffer);

				// update ball position
				ball.updatePos();
			}

			// check ball collision with wall
			if (ball.xPos() + ball.size()/2 > windowWidth ||
					ball.xPos() - ball.size()/2 < 0) {
				ball.invertXDir();
			} else if (ball.yPos() - ball.size()/2 < 0) {
				ball.invertYDir();
			} else if (ball.yPos() + ball.size()/2 > windowHeight) {
				// hit bottom of screen hence user lost the game
				string endGameText = "You have lost with a total score of " + currentScore.getText() +
        				             "! Press 'r' to play again or 'q' to quit.";
				endGameMessage.update(endGameText);
				gameInPlay = false;
			}
			
			// check ball collision with paddle
			bool ballLeftOfPaddleNow = ball.xPos() - ball.size()/2 < paddle.xPos() + paddle.width();
      bool ballRightOfPaddleNow = ball.xPos() + ball.size()/2 > paddle.xPos();
      bool ballAbovePaddleNow = ball.yPos() - ball.size()/2 < paddle.yPos() + paddle.height();
      bool ballBelowPaddleNow = ball.yPos() + ball.size()/2 > paddle.yPos();

      if (ballLeftOfPaddleNow && ballRightOfPaddleNow && ballAbovePaddleNow && ballBelowPaddleNow) {
        if (!ballLeftOfPaddle || !ballRightOfPaddle) {
     	  	ball.invertXDir();
        } else if (!ballBelowPaddle) { // don't interact with hit detection from below paddle
          ball.invertYDir();

					// hit ball to the left if it hits the left half of the paddle, else hit it to the right
					if (ball.xPos() > paddle.xPos() + paddle.width()/2) {
						if (ball.xDir() < 0) ball.invertXDir();
					} else {
						if (ball.xDir() > 0) ball.invertXDir();
					}
        }
      }
			// save flags to detect the side of the block that was hit on collision
      ballLeftOfPaddle = ballLeftOfPaddleNow;
      ballRightOfPaddle = ballRightOfPaddleNow;
      ballAbovePaddle = ballAbovePaddleNow;
      ballBelowPaddle = ballBelowPaddleNow;
			
			// check ball collision with with blocks
			for (auto &blockinfo: blocks) {
				Block *block = &(blockinfo.block);

				if (block->isDestroyed()) continue;

				bool ballLeftOfBlockNow = ball.xPos() - ball.size()/2 < block->xPos() + block->width();
				bool ballRightOfBlockNow = ball.xPos() + ball.size()/2 > block->xPos();
				bool ballAboveBlockNow = ball.yPos() - ball.size()/2 < block->yPos() + block->height();
				bool ballBelowBlockNow = ball.yPos() + ball.size()/2 > block->yPos();

				if (ballLeftOfBlockNow && ballRightOfBlockNow && ballAboveBlockNow && ballBelowBlockNow) {
					if (!blockinfo.ballLeftOfBlock || !blockinfo.ballRightOfBlock) {
          	ball.invertXDir();
					} else {
          	ball.invertYDir();
					}
					block->onHit();
					// semi-randomly select colour for ball
					ball.replaceGC(gcs[rand() % gcs.size()]);
					currentScore.update(to_string(stoi(currentScore.getText()) + 1));
					break;
				}
				// save flags to detect the side of the block that was hit on collision
				blockinfo.ballLeftOfBlock = ballLeftOfBlockNow;
				blockinfo.ballRightOfBlock = ballRightOfBlockNow;
				blockinfo.ballAboveBlock = ballAboveBlockNow;
				blockinfo.ballBelowBlock = ballBelowBlockNow;
			}

			// reset game state
			int score = stoi(currentScore.getText());
			if (score > 0 && score % 50 == 0) { // hey marker, change 50 to a low number to replicate reset
        resetGameState(blocks, ball, paddle, currentScore);
      }

			// copy buffer to window
      XCopyArea(display, buffer, window, gcBlack,
                0, 0, windowWidth, windowHeight,  // region of pixmap to copy
                0, 0); // position to put top left corner of pixmap in window

			XFlush( display );

			lastRepaint = now(); // remember when the paint happened
		}

		// IMPORTANT: sleep for a bit to let other processes work
		if (XPending(display) == 0) {
			usleep(1000000 / FPS - (now() - lastRepaint));
		}
	}
	XCloseDisplay(display);
}
