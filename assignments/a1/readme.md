Hi! This is a simple Breakout! game created using XLib in C++. To play the game, use the buttons
'a' and 'd' to move the paddle left and right respectively to prevent the ball from hitting the 
bottom of the screen. You can press 'q' at anytime to quit the game. The score can be seen
in the bottom right hand side of the screen. To run the game, simply run make and then 
'./breakout [FPS] [BallSpeed]' where 10 <= FPS <= 60 and 1 <= BallSpeed <= 10.

Some noticable features are that hitting the ball using the left half of the paddle will push 
the ball to the left while hitting on the right side of the paddle will push the ball to the 
right. You can use this mechanic to drive the ball to whereever you want. The blocks are also
coloured and when you hit a block, the ball will "semi" randomly select another colour to change
to!
