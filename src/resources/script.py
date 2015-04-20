import colorsys,sys,re
from pymol import cmd
cmd.show_as("cartoon")

def colorrgb(rgb, str):
	cmd.set_color("color"+str, rgb)
	cmd.color("color"+str, str)
	cmd.disable(str)
